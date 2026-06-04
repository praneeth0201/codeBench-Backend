package com.codeBench.demo.Controller;

import com.codeBench.demo.DTO.RunRequest;
import com.codeBench.demo.Entity.RunRecord;
import com.codeBench.demo.Services.RunService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for "Run" (sample-test-case-only execution).
 *
 * Flow:
 *  1. Frontend subscribes to  /topic/run/{runId}  via STOMP/WebSocket
 *  2. Frontend calls  POST /api/run  → gets back { id, status:"PENDING", … }
 *  3. Backend processes the run asynchronously via RabbitMQ
 *  4. RunWorker pushes the final RunRecord to  /topic/run/{runId}  when done
 */
@RestController
@RequestMapping("/api/run")
public class RunController {

    private final RunService runService;

    public RunController(RunService runService) {
        this.runService = runService;
    }

    /**
     * Accepts code + problemId + language, saves a RunRecord (PENDING),
     * and enqueues it for async execution.
     * Returns the RunRecord immediately — frontend uses its {@code id}
     * to subscribe to  /topic/run/{id}  for the final result.
     */
    @PostMapping
    public ResponseEntity<RunRecord> run(
            @Valid @RequestBody RunRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        RunRecord record = runService.createRun(username, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(record);
    }
}
