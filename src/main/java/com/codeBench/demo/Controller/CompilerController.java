package com.codeBench.demo.Controller;

import com.codeBench.demo.DTO.CompilerRequest;
import com.codeBench.demo.Services.CompilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/compiler")
public class CompilerController {

    @Autowired
    private CompilerService compilerService;

    @PostMapping("/run")
    public Map<String, String> run(
            @RequestBody CompilerRequest request
    ) throws Exception {

        String sessionId =
                compilerService.startSession(request);

        return Map.of(
                "sessionId",
                sessionId
        );
    }
}
