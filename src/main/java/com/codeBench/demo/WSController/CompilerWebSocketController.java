package com.codeBench.demo.WSController;

import com.codeBench.demo.DTO.CompilerInput;
import com.codeBench.demo.Services.CompilerSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

@Controller
public class CompilerWebSocketController {

    @Autowired
    private CompilerSessionManager sessionManager;

    @MessageMapping("/compiler/input")
    public void input(
            CompilerInput input
    ) throws Exception {

        Process process =
                sessionManager.getSession(
                        input.getSessionId()
                );

        BufferedWriter writer =
                new BufferedWriter(
                        new OutputStreamWriter(
                                process.getOutputStream()
                        )
                );

        writer.write(input.getInput());

        writer.newLine();

        writer.flush();
    }
}
