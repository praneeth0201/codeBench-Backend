package com.codeBench.demo.Services;

import com.codeBench.demo.DAO.SubmissionRepository;
import com.codeBench.demo.DAO.TestCaseRepository;
import com.codeBench.demo.Entity.Submission;
import com.codeBench.demo.Configuration.RabbitConfig;
import com.codeBench.demo.Entity.TestCase;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.io.BufferedWriter;

import java.io.*;
import java.util.List;

@Service
public class SubmissionWorker {

    private final SubmissionRepository submissionRepository;
    private final TestCaseRepository testCaseRepository;
    public SubmissionWorker(SubmissionRepository submissionRepository,TestCaseRepository testCaseRepository) {
        this.submissionRepository = submissionRepository;
        this.testCaseRepository=testCaseRepository;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void processSubmission(Long submissionId) {

        Submission submission = submissionRepository.findById(submissionId).orElseThrow();

        try {
            submission.setStatus("RUNNING");
            submissionRepository.save(submission);

            File file = createTempFile(submission);

            List<TestCase> testCases =
                    testCaseRepository.findByProblemId(submission.getProblemId());

            long start = System.currentTimeMillis();

            boolean allPassed = true;
            StringBuilder finalOutput = new StringBuilder();

            for (TestCase testCase : testCases) {

                String output = executeCodeWithInput(
                        file,
                        submission.getLanguage(),
                        testCase.getInput()
                );

                output = output.trim();
                String expected = testCase.getExpectedOutput().trim();

                if (!output.equals(expected)) {
                    allPassed = false;

                    finalOutput.append("FAILED\n");
                    finalOutput.append("Input: ").append(testCase.getInput()).append("\n");
                    finalOutput.append("Expected: ").append(expected).append("\n");
                    finalOutput.append("Got: ").append(output).append("\n");

                    break;
                }
            }

            long end = System.currentTimeMillis();

            if (allPassed) {
                submission.setStatus("ACCEPTED");
                submission.setOutput("All test cases passed");
            } else {
                submission.setStatus("WRONG_ANSWER");
                submission.setOutput(finalOutput.toString());
            }

            submission.setExecutionTime(end - start);

            file.delete();

        } catch (Exception e) {
            submission.setStatus("RUNTIME_ERROR");
            submission.setOutput(e.getMessage());
        }

        submissionRepository.save(submission);
    }

    private File createTempFile(Submission submission) throws IOException {
        String extension = submission.getLanguage().equals("python") ? ".py" : ".txt";

        File file = File.createTempFile("code_", extension);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(submission.getCode().trim());
            writer.write("\n");

        }

        return file;
    }

//    private String executeCode(File file, String language)
//            throws IOException, InterruptedException {
//
//        ProcessBuilder processBuilder;
//
//        if (language.equals("python")) {
//            processBuilder = new ProcessBuilder(
//                    "docker", "run", "--rm",
//                    "-v", file.getParent() + ":/app",
//                    "python:3.9",
//                    "python", "/app/" + file.getName()
//            );
//        } else {
//            throw new RuntimeException("Language not supported");
//        }
//
//        processBuilder.redirectErrorStream(true);
//
//        Process process = processBuilder.start();
//
//        BufferedReader reader = new BufferedReader(
//                new InputStreamReader(process.getInputStream())
//        );
//
//        StringBuilder output = new StringBuilder();
//        String line;
//
//        while ((line = reader.readLine()) != null) {
//            output.append(line).append("\n");
//        }
//
//        process.waitFor();
//
//        return output.toString();
//    }

    private String executeCodeWithInput(File file, String language, String input)
            throws IOException, InterruptedException {

        if (language.equals("python")) {
            return executePython(file,input);
        }
        else if(language.equals("java")){
            return executeJava(file,input);
        }
        else if(language.equals("cpp")){
            return executeCpp(file,input);
        }
        else {
            throw new RuntimeException("Language not supported");
        }

    }



    private String executeJava(File file, String input)
            throws IOException, InterruptedException {


        File javaFile = new File(file.getParent(), "Main.java");

        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(javaFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }

        ProcessBuilder processBuilder = new ProcessBuilder(
                "docker", "run", "--rm",
                "-i",
                "--network", "none",
                "--memory", "200m",
                "--cpus", "1",
                "-v", javaFile.getParent() + ":/app",
                "eclipse-temurin:17",
                "sh", "-c",
                "javac /app/Main.java && timeout 5s java -cp /app Main"
        );


        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();


        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(process.getOutputStream())
        );

        if (input != null && !input.isEmpty()) {
            writer.write(input);
            writer.newLine();
        } else {
            writer.write("\n");
        }

        writer.flush();
        writer.close();


        boolean finished = process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS);

        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("TIME_LIMIT_EXCEEDED");
        }

        // 🔥 read output (combined stdout + stderr)
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = process.exitValue();

        if (exitCode != 0) {
            throw new RuntimeException(output.toString().replace("/app/", "").trim());
        }

        return output.toString();
    }

    private String executePython(File file,String input)throws IOException, InterruptedException{

        ProcessBuilder processBuilder = new ProcessBuilder(
                "docker", "run", "--rm",
                "-i",
                "--network", "none",
                "--memory", "100m",
                "--cpus", "0.5",
                "-v", file.getParent() + ":/app",
                "python:3.9",
                "sh", "-c",
                "timeout 5s python /app/" + file.getName()
        );

        processBuilder.redirectErrorStream(false);

        Process process = processBuilder.start();

// send input
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(process.getOutputStream())
        );

        if (input != null && !input.isEmpty()) {
            writer.write(input);
            writer.newLine();
        } else {
            writer.write("\n");
        }

        writer.flush();
        writer.close();

// timeout
        boolean finished = process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS);

        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("TIME_LIMIT_EXCEEDED");
        }

// read stdout
        BufferedReader stdoutReader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        StringBuilder output = new StringBuilder();
        String line;
        while ((line = stdoutReader.readLine()) != null) {
            output.append(line).append("\n");
        }

// read stderr
        BufferedReader stderrReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream())
        );

        StringBuilder errorOutput = new StringBuilder();
        while ((line = stderrReader.readLine()) != null) {
            errorOutput.append(line).append("\n");
        }

        int exitCode = process.exitValue();

        if (exitCode != 0) {
            String fullError = (errorOutput.length() > 0
                    ? errorOutput.toString()
                    : output.toString()).trim();

            throw new RuntimeException(fullError.replace("/app/", ""));
        }

        return output.toString();
    }

    private String executeCpp(File file, String input)
            throws IOException, InterruptedException {

        File cppFile = new File(file.getParent(), "main.cpp");

        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(cppFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }

        ProcessBuilder processBuilder = new ProcessBuilder(
                "docker", "run", "--rm",
                "-i",
                "--network", "none",
                "--memory", "200m",
                "--cpus", "1",
                "-v", cppFile.getParent() + ":/app",
                "gcc:latest",
                "sh", "-c",
                "g++ /app/main.cpp -o /app/main && timeout 5s /app/main"
        );

        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(process.getOutputStream())
        );

        if (input != null && !input.isEmpty()) {
            writer.write(input);
            writer.newLine();
        } else {
            writer.write("\n");
        }

        writer.flush();
        writer.close();

        boolean finished = process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS);

        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("TIME_LIMIT_EXCEEDED");
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = process.exitValue();

        if (exitCode != 0) {
            throw new RuntimeException(output.toString().replace("/app/", "").trim());
        }

        return output.toString();
    }
}
