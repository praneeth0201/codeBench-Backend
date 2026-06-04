package com.codeBench.demo.Services;

import com.codeBench.demo.Configuration.RabbitConfig;
import com.codeBench.demo.DAO.RunRecordRepository;
import com.codeBench.demo.DAO.SampleTestCaseRepository;
import com.codeBench.demo.Entity.RunRecord;
import com.codeBench.demo.Entity.SampleTestCase;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

import tools.jackson.databind.ObjectMapper;
import com.codeBench.demo.DTO.RunResultDTO;
import com.codeBench.demo.DTO.SampleResultDTO;

/**
 * Mirrors SubmissionWorker but:
 *  - Listens on run_queue instead of submission_queue.
 *  - Runs against SampleTestCases (visible test cases) only.
 *  - Does NOT update acceptanceRate / totalSubmissions counters.
 *  - Stores per-sample result detail so the frontend can show pass/fail per case.
 */
@Service
public class RunWorker {

    private final RunRecordRepository runRecordRepository;
    private final SampleTestCaseRepository sampleTestCaseRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public RunWorker(RunRecordRepository runRecordRepository,
                     SampleTestCaseRepository sampleTestCaseRepository,
                     SimpMessagingTemplate messagingTemplate) {
        this.runRecordRepository = runRecordRepository;
        this.sampleTestCaseRepository = sampleTestCaseRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = RabbitConfig.RUN_QUEUE_NAME)
    public void processRun(Long runId) {

        RunRecord run = runRecordRepository.findById(runId).orElseThrow();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            run.setStatus("RUNNING");
            runRecordRepository.save(run);

            File file = null;
            try {
                file = createTempFile(run);

                List<SampleTestCase> samples =
                        sampleTestCaseRepository.findByProblemIdOrderByOrderIndexAsc(run.getProblemId());

            long start = System.currentTimeMillis();

            List<SampleResultDTO> sampleResults = new ArrayList<>();
            boolean allPassed = true;

            for (SampleTestCase sample : samples) {

                String actual;
                String verdict;
                String errorMsg = null;

                try {
                    actual = executeCodeWithInput(file, run.getLanguage(), sample.getInput()).trim();
                    String expected = sample.getExpectedOutput().trim();

                    if (actual.equals(expected)) {
                        verdict = "PASSED";
                    } else {
                        verdict = "FAILED";
                        allPassed = false;
                    }

                } catch (Exception e) {
                    allPassed = false;
                    verdict = "FAILED";
                    actual = "";
                    errorMsg = e.getMessage();
                }

                String got = errorMsg != null ? "Error: " + errorMsg : actual;

                sampleResults.add(new SampleResultDTO(
                        sample.getOrderIndex(),
                        sample.getInput(),
                        sample.getExpectedOutput().trim(),
                        got,
                        verdict
                ));
            }

            long end = System.currentTimeMillis();
            long execTime = end - start;

                run.setStatus("COMPLETED");
                run.setOutput(objectMapper.writeValueAsString(sampleResults));
                run.setExecutionTime(execTime);

                runRecordRepository.save(run);

                RunResultDTO resultDTO = new RunResultDTO(
                        run.getId(),
                        "COMPLETED",
                        allPassed ? "ACCEPTED" : "FAILED",
                        execTime,
                        sampleResults
                );

                // Push the final result to the frontend via WebSocket
                messagingTemplate.convertAndSend(
                        "/topic/run/" + run.getId(),
                        resultDTO
                );
            } finally {
                try {
                    if (file != null && file.exists()) {
                        file.delete();
                        // Also try to clean up Main.java, main.cpp, main etc if they were created in the temp directory.
                        new File(file.getParent(), "Main.java").delete();
                        new File(file.getParent(), "Main.class").delete();
                        new File(file.getParent(), "main.cpp").delete();
                        new File(file.getParent(), "main").delete();
                        new File(file.getParent(), "main.exe").delete();
                    }
                } catch (Exception ignored) {}
            }

        } catch (Exception e) {
            run.setStatus("RUNTIME_ERROR");
            run.setOutput(e.getMessage());
            runRecordRepository.save(run);

            RunResultDTO errorDTO = new RunResultDTO(
                    run.getId(),
                    "RUNTIME_ERROR",
                    "FAILED",
                    0L,
                    new ArrayList<>()
            );

            messagingTemplate.convertAndSend(
                    "/topic/run/" + run.getId(),
                    errorDTO
            );
        }
    }

    // ── File creation ─────────────────────────────────────────────────────────

    private File createTempFile(RunRecord run) throws IOException {
        String extension = run.getLanguage().equals("python") ? ".py" : ".txt";
        File file = File.createTempFile("run_", extension);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(run.getCode().trim());
            writer.write("\n");
        }
        return file;
    }

    // ── Language dispatch (identical logic to SubmissionWorker) ───────────────

    private String executeCodeWithInput(File file, String language, String input)
            throws IOException, InterruptedException {

        return switch (language) {
            case "python" -> executePython(file, input);
            case "java"   -> executeJava(file, input);
            case "cpp"    -> executeCpp(file, input);
            default -> throw new RuntimeException("Language not supported: " + language);
        };
    }

    private String executeJava(File file, String input) throws IOException, InterruptedException {

        File javaFile = new File(file.getParent(), "Main.java");
        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(javaFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }

        ProcessBuilder pb = new ProcessBuilder(
                "docker", "run", "--rm", "-i",
                "--network", "none",
                "--memory", "200m",
                "--cpus", "1",
                "-v", javaFile.getParent() + ":/app",
                "eclipse-temurin:17",
                "sh", "-c", "javac /app/Main.java && timeout 5s java -cp /app Main"
        );
        pb.redirectErrorStream(true);

        return runProcess(pb, input);
    }

    private String executePython(File file, String input) throws IOException, InterruptedException {

        ProcessBuilder pb = new ProcessBuilder(
                "docker", "run", "--rm", "-i",
                "--network", "none",
                "--memory", "100m",
                "--cpus", "0.5",
                "-v", file.getParent() + ":/app",
                "python:3.9",
                "sh", "-c", "timeout 5s python /app/" + file.getName()
        );
        pb.redirectErrorStream(false);

        Process process = pb.start();
        sendInput(process, input);

        boolean finished = process.waitFor(15, java.util.concurrent.TimeUnit.SECONDS);
        if (!finished) { process.destroyForcibly(); throw new RuntimeException("TIME_LIMIT_EXCEEDED"); }

        String stdout = readStream(process.getInputStream());
        String stderr = readStream(process.getErrorStream());

        int exitCode = process.exitValue();
        if (exitCode == 124 || exitCode == 143) {
            throw new RuntimeException("TIME_LIMIT_EXCEEDED");
        }

        if (exitCode != 0) {
            String err = stderr.isBlank() ? stdout : stderr;
            throw new RuntimeException(err.replace("/app/", "").trim());
        }
        return stdout;
    }

    private String executeCpp(File file, String input) throws IOException, InterruptedException {

        File cppFile = new File(file.getParent(), "main.cpp");
        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(cppFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }

        ProcessBuilder pb = new ProcessBuilder(
                "docker", "run", "--rm", "-i",
                "--network", "none",
                "--memory", "200m",
                "--cpus", "1",
                "-v", cppFile.getParent() + ":/app",
                "gcc:latest",
                "sh", "-c", "g++ /app/main.cpp -o /app/main && timeout 5s /app/main"
        );
        pb.redirectErrorStream(true);

        return runProcess(pb, input);
    }

    // ── Shared helpers ────────────────────────────────────────────────────────

    private String runProcess(ProcessBuilder pb, String input) throws IOException, InterruptedException {
        Process process = pb.start();
        sendInput(process, input);

        boolean finished = process.waitFor(20, java.util.concurrent.TimeUnit.SECONDS);
        if (!finished) { process.destroyForcibly(); throw new RuntimeException("TIME_LIMIT_EXCEEDED"); }

        String output = readStream(process.getInputStream());
        
        int exitCode = process.exitValue();
        if (exitCode == 124 || exitCode == 143) {
            throw new RuntimeException("TIME_LIMIT_EXCEEDED");
        }
        
        if (exitCode != 0) {
            throw new RuntimeException(output.replace("/app/", "").trim());
        }
        return output;
    }

    private void sendInput(Process process, String input) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
            writer.write(input != null && !input.isEmpty() ? input : "\n");
            writer.newLine();
            writer.flush();
        }
    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}
