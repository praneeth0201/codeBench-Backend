package com.codeBench.demo.Services;

import com.codeBench.demo.DAO.ProblemRepository;
import com.codeBench.demo.DAO.SubmissionRepository;
import com.codeBench.demo.DAO.TestCaseRepository;
import com.codeBench.demo.Entity.Problem;
import com.codeBench.demo.Entity.Submission;
import com.codeBench.demo.Entity.SubmissionStatus;
import com.codeBench.demo.Entity.Verdict;
import com.codeBench.demo.Configuration.RabbitConfig;
import com.codeBench.demo.Entity.TestCase;
import com.codeBench.demo.DTO.SubmissionResultDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class SubmissionWorker {

    private final SubmissionRepository submissionRepository;
    private final TestCaseRepository testCaseRepository;
    private final ProblemRepository problemRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String DELIMITER = "---TC_DONE---";

    public SubmissionWorker(SubmissionRepository submissionRepository,
                            TestCaseRepository testCaseRepository,
                            ProblemRepository problemRepository,
                            SimpMessagingTemplate messagingTemplate) {
        this.submissionRepository = submissionRepository;
        this.testCaseRepository = testCaseRepository;
        this.problemRepository = problemRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void processSubmission(Long submissionId) {

        Submission submission = submissionRepository.findById(submissionId).orElseThrow();
        Path tempDir = null;

        try {
            submission.setStatus(SubmissionStatus.RUNNING);
            submissionRepository.save(submission);

            List<TestCase> testCases = testCaseRepository.findByProblemId(submission.getProblemId());

            submission.setTotalTestCases(testCases.size());

            if (testCases.isEmpty()) {
                submission.setStatus(SubmissionStatus.COMPLETED);
                submission.setVerdict(Verdict.ACCEPTED);
                submission.setOutput("No test cases found.");
                submission.setExecutionTime(0L);
                submission.setPassedTestCases(0);
                submissionRepository.save(submission);
                sendDto(submission);
                return;
            }

            tempDir = Files.createTempDirectory("submission_" + submissionId + "_");

            long start = System.nanoTime();
            String[] results = executeAllTestCases(tempDir, submission, testCases);
            long end = System.nanoTime();

            boolean allPassed = true;
            StringBuilder finalOutput = new StringBuilder();

            int passedCount = 0;
            boolean firstFailRecorded = false;

            for (int i = 0; i < testCases.size(); i++) {
                String actual = results[i].trim();
                String expected = testCases.get(i).getExpectedOutput().trim();

                if (!actual.equals(expected)) {
                    allPassed = false;
                    if (!firstFailRecorded) {
                        submission.setFailedInput(testCases.get(i).getInput());
                        submission.setExpectedOutput(expected);
                        submission.setActualOutput(actual);
                        firstFailRecorded = true;
                    }
                    finalOutput.append("FAILED on Test Case ").append(i + 1).append("\n");
                    finalOutput.append("Input: ").append(testCases.get(i).getInput()).append("\n");
                    finalOutput.append("Expected: ").append(expected).append("\n");
                    finalOutput.append("Got: ").append(actual).append("\n");
                    // Do not break; evaluate all hidden test cases
                } else {
                    passedCount++;
                }
            }

            submission.setPassedTestCases(passedCount);

            if (allPassed) {
                submission.setVerdict(Verdict.ACCEPTED);
                submission.setOutput("All " + testCases.size() + " test cases passed");
            } else {
                submission.setVerdict(Verdict.WRONG_ANSWER);
                submission.setOutput(finalOutput.toString());
            }

            submission.setStatus(SubmissionStatus.COMPLETED);
            submission.setExecutionTime((end - start) / 1_000_000);

        } catch (Exception e) {
            submission.setStatus(SubmissionStatus.COMPLETED);
            String msg = e.getMessage() != null ? e.getMessage() : "Unknown error";
            if (msg.startsWith("TIME_LIMIT_EXCEEDED")) {
                submission.setVerdict(Verdict.TIME_LIMIT_EXCEEDED);
                submission.setOutput("Your code exceeded the time limit.");
            } else if (msg.startsWith("COMPILATION_ERROR:")) {
                submission.setVerdict(Verdict.COMPILATION_ERROR);
                String cError = msg.substring("COMPILATION_ERROR:".length()).trim();
                submission.setCompileError(cError);
                submission.setOutput(cError);
            } else {
                submission.setVerdict(Verdict.RUNTIME_ERROR);
                submission.setOutput(msg);
            }
            if (submission.getPassedTestCases() == null) {
                submission.setPassedTestCases(0);
            }
        } finally {
            if (tempDir != null) deleteDirectory(tempDir.toFile());
        }

        submissionRepository.save(submission);

        // Update problem statistics
        problemRepository.findById(submission.getProblemId()).ifPresent(problem -> {
            long totalSubs = (problem.getTotalSubmissions() != null ? problem.getTotalSubmissions() : 0L) + 1;
            long totalAcc  = (problem.getTotalAccepted()    != null ? problem.getTotalAccepted()    : 0L);
            if (Verdict.ACCEPTED.equals(submission.getVerdict())) {
                totalAcc++;
            }
            double rate = totalSubs > 0 ? (totalAcc * 100.0 / totalSubs) : 0.0;
            problem.setTotalSubmissions(totalSubs);
            problem.setTotalAccepted(totalAcc);
            problem.setAcceptanceRate(Math.round(rate * 100.0) / 100.0); // round to 2 decimal places
            problemRepository.save(problem);
        });

        sendDto(submission);
    }

    private void sendDto(Submission submission) {
        SubmissionResultDTO dto = new SubmissionResultDTO(
            submission.getId(),
            submission.getStatus(),
            submission.getVerdict(),
            submission.getExecutionTime(),
            submission.getPassedTestCases(),
            submission.getTotalTestCases(),
            submission.getFailedInput(),
            submission.getExpectedOutput(),
            submission.getActualOutput(),
            submission.getCompileError()
        );
        messagingTemplate.convertAndSend("/topic/submission/" + submission.getId(), dto);
    }

    /**
     * Writes runner.sh + source code to tempDir, runs one Docker container
     * for ALL test cases. Returns one output string per test case.
     */
    private String[] executeAllTestCases(Path tempDir, Submission submission, List<TestCase> testCases)
            throws IOException, InterruptedException {

        String language = submission.getLanguage();
        int timeLimitSeconds = 5;

        // 1. Write the source code file
        String sourceFileName = getSourceFileName(language);
        File sourceFile = tempDir.resolve(sourceFileName).toFile();
        try (FileWriter fw = new FileWriter(sourceFile)) {
            fw.write(submission.getCode().trim());
            fw.write("\n");
        }

        // 2. Write runner.sh — this runs inside the container
        File runnerScript = tempDir.resolve("runner.sh").toFile();
        writeRunnerScript(runnerScript, language, sourceFileName, testCases.size(), timeLimitSeconds);

        // 3. Build docker command (uses bash, not sh)
        String image = getImage(language);
        String memory = language.equals("java") ? "300m" : "150m";
        int totalTimeout = (timeLimitSeconds * testCases.size()) + 30;

        List<String> dockerCmd = List.of(
            "docker", "run", "--rm",
            "-i",
            "--network", "none",
            "--memory", memory,
            "--cpus", "1",
            "-v", tempDir.toAbsolutePath() + ":/app",
            image,
            "bash", "/app/runner.sh"
        );

        ProcessBuilder pb = new ProcessBuilder(dockerCmd);
        pb.redirectErrorStream(false);

        Process process = pb.start();

        // 4. Feed all test case inputs via stdin (separated by DELIMITER)
        try (BufferedWriter stdin = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
            for (TestCase tc : testCases) {
                stdin.write(tc.getInput().trim());
                stdin.write("\n");
                stdin.write(DELIMITER + "\n");
            }
        }

        // 5. Read stdout and stderr concurrently to avoid blocking
        StringBuilder stdoutBuf = new StringBuilder();
        StringBuilder stderrBuf = new StringBuilder();

        Thread stdoutThread = new Thread(() -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    stdoutBuf.append(line).append("\n");
                }
            } catch (IOException ignored) {}
        });

        Thread stderrThread = new Thread(() -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    stderrBuf.append(line).append("\n");
                }
            } catch (IOException ignored) {}
        });

        stdoutThread.start();
        stderrThread.start();

        boolean finished = process.waitFor(totalTimeout + 10L, java.util.concurrent.TimeUnit.SECONDS);
        stdoutThread.join(5000);
        stderrThread.join(5000);

        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("TIME_LIMIT_EXCEEDED");
        }

        int exitCode = process.exitValue();
        String stdout = stdoutBuf.toString();
        String stderr = stderrBuf.toString();

        // If container exited with error and no DELIMITER markers appeared → compile/crash error
        if (exitCode != 0 && !stdout.contains(DELIMITER)) {
            String errorMsg = stderr.isBlank() ? stdout : stderr;
            throw new RuntimeException("COMPILATION_ERROR:" + errorMsg.replace("/app/", "").trim());
        }

        // 6. Split stdout by DELIMITER to get per-test-case outputs
        String[] parts = stdout.split(DELIMITER + "\n?", -1);
        String[] results = new String[testCases.size()];
        for (int i = 0; i < testCases.size(); i++) {
            results[i] = (i < parts.length) ? parts[i].trim() : "";
        }

        return results;
    }

    /**
     * Writes a bash runner script to disk.
     * The script:
     *   - Compiles the code (for Java/C++)
     *   - Loops N times, each iteration:
     *       - reads lines from stdin until it sees DELIMITER
     *       - feeds them to the program
     *       - echoes DELIMITER after
     */
    private void writeRunnerScript(File scriptFile, String language, String sourceFileName,
                                   int testCaseCount, int timeLimitSeconds) throws IOException {

        StringBuilder sb = new StringBuilder();
        sb.append("#!/bin/bash\n");
        sb.append("set -e\n\n");

        // Compilation step
        if (language.equals("java")) {
            sb.append("javac /app/").append(sourceFileName).append(" 2>&1\n");
            sb.append("if [ $? -ne 0 ]; then exit 1; fi\n\n");
        } else if (language.equals("cpp")) {
            sb.append("g++ /app/").append(sourceFileName).append(" -o /app/main 2>&1\n");
            sb.append("if [ $? -ne 0 ]; then exit 1; fi\n\n");
        }

        // Remove 'set -e' before the loop so a runtime error on one test case
        // doesn't kill the whole container
        sb.append("set +e\n\n");

        // Loop over test cases
        sb.append("for i in $(seq 1 ").append(testCaseCount).append("); do\n");
        sb.append("  INPUT=''\n");
        sb.append("  while IFS= read -r line; do\n");
        sb.append("    if [ \"$line\" = \"").append(DELIMITER).append("\" ]; then\n");
        sb.append("      break\n");
        sb.append("    fi\n");
        sb.append("    INPUT=\"${INPUT}${line}\n\"\n");
        sb.append("  done\n\n");

        // Run the program with piped input
        if (language.equals("java")) {
            sb.append("  printf '%s' \"$INPUT\" | timeout ").append(timeLimitSeconds)
              .append("s java -cp /app Main 2>&1\n");
        } else if (language.equals("python")) {
            sb.append("  printf '%s' \"$INPUT\" | timeout ").append(timeLimitSeconds)
              .append("s python /app/").append(sourceFileName).append(" 2>&1\n");
        } else {
            sb.append("  printf '%s' \"$INPUT\" | timeout ").append(timeLimitSeconds)
              .append("s /app/main 2>&1\n");
        }

        sb.append("  echo '").append(DELIMITER).append("'\n");
        sb.append("done\n");

        try (FileWriter fw = new FileWriter(scriptFile)) {
            fw.write(sb.toString());
        }
    }

    private String getSourceFileName(String language) {
        return switch (language) {
            case "java" -> "Main.java";
            case "python" -> "solution.py";
            default -> "main.cpp";
        };
    }

    private String getImage(String language) {
        return switch (language) {
            case "java" -> "eclipse-temurin:17";
            case "python" -> "python:3.9";
            default -> "gcc:latest";
        };
    }

    private void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) deleteDirectory(child);
            }
        }
        dir.delete();
    }
}
