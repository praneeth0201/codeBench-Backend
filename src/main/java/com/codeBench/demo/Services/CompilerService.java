package com.codeBench.demo.Services;

import com.codeBench.demo.DTO.CompilerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.UUID;

@Service
public class CompilerService {

    @Autowired
    private CompilerSessionManager sessionManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public String startSession(
            CompilerRequest request
    ) throws Exception {

        String sessionId =
                UUID.randomUUID().toString();

        File file;

        ProcessBuilder processBuilder;

        if (request.getLanguage().equals("java")) {

            File dir =
                    Files.createTempDirectory(
                            "compiler"
                    ).toFile();

            file = new File(dir, "Main.java");

            FileWriter fw = new FileWriter(file);

            fw.write(request.getCode());

            fw.close();

            processBuilder =
                    new ProcessBuilder(

                            "docker", "run", "--rm",

                            "-i",

                            "--network", "none",

                            "--memory", "200m",

                            "--cpus", "1",

                            "-v",
                            dir.getAbsolutePath() + ":/app",

                            "eclipse-temurin:17",

                            "sh", "-c",

                            "javac /app/Main.java && java -cp /app Main"
                    );

        } else if (
                request.getLanguage().equals("python")
        ) {

            File dir =
                    Files.createTempDirectory(
                            "compiler"
                    ).toFile();

            file = new File(dir, "main.py");

            FileWriter fw = new FileWriter(file);

            fw.write(request.getCode());

            fw.close();

            processBuilder =
                    new ProcessBuilder(

                            "docker", "run", "--rm",

                            "-i",

                            "--network", "none",

                            "--memory", "100m",

                            "--cpus", "0.5",

                            "-v",
                            dir.getAbsolutePath() + ":/app",

                            "python:3.9",

                            "python",
                            "/app/main.py"
                    );

        } else if (
                request.getLanguage().equals("cpp")
        ) {

            File dir =
                    Files.createTempDirectory(
                            "compiler"
                    ).toFile();

            file = new File(dir, "main.cpp");

            FileWriter fw = new FileWriter(file);

            fw.write(request.getCode());

            fw.close();

            processBuilder =
                    new ProcessBuilder(

                            "docker", "run", "--rm",

                            "-i",

                            "--network", "none",

                            "--memory", "200m",

                            "--cpus", "1",

                            "-v",
                            dir.getAbsolutePath() + ":/app",

                            "gcc:latest",

                            "sh", "-c",

                            "g++ /app/main.cpp -o /app/main && /app/main"
                    );

        } else {

            throw new RuntimeException(
                    "Language not supported"
            );
        }

        Process process =
                processBuilder.start();

        sessionManager.addSession(
                sessionId,
                process
        );

        streamOutput(sessionId, process);

        cleanupAfterExit(sessionId, process);

        return sessionId;
    }

    private void streamOutput(
            String sessionId,
            Process process
    ) {

        new Thread(() -> {

            try {

                BufferedReader stdout =
                        new BufferedReader(
                                new InputStreamReader(
                                        process.getInputStream()
                                )
                        );

                String line;

                int maxOutputSize = 10000;

                StringBuilder totalOutput = new StringBuilder();

                while ((line = stdout.readLine()) != null) {

                    totalOutput.append(line).append("\n");

                    if (totalOutput.length() > maxOutputSize) {

                        messagingTemplate.convertAndSend(
                                "/topic/compiler/" + sessionId,
                                "OUTPUT_LIMIT_EXCEEDED"
                        );

                        process.destroyForcibly();

                        sessionManager.removeSession(sessionId);

                        break;
                    }

                    messagingTemplate.convertAndSend(
                            "/topic/compiler/" + sessionId,
                            line
                    );
                }

            } catch (Exception e) {

                e.printStackTrace();
            }

        }).start();

        new Thread(() -> {

            try {

                BufferedReader stderr =
                        new BufferedReader(
                                new InputStreamReader(
                                        process.getErrorStream()
                                )
                        );

                String line;

                while ((line = stderr.readLine()) != null) {

                    messagingTemplate.convertAndSend(
                            "/topic/compiler/" + sessionId,
                            line
                    );
                }

            } catch (Exception e) {

                e.printStackTrace();
            }

        }).start();
    }

    private void cleanupAfterExit(
            String sessionId,
            Process process
    ) {

        new Thread(() -> {

            try {

                process.waitFor();

                sessionManager.removeSession(sessionId);

            } catch (Exception e) {

                e.printStackTrace();
            }

        }).start();
    }
}
