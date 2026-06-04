package com.codeBench.demo.Controller;

import com.codeBench.demo.DTO.SubmissionRequest;
import com.codeBench.demo.DTO.SubmissionCreatedDTO;
import com.codeBench.demo.DTO.SubmissionResultDTO;
import com.codeBench.demo.Services.SubmissionService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Submission flow:
 *  1. Frontend subscribes to  /topic/submission/{submissionId}  via STOMP
 *  2. POST /api/submissions  → returns 202 Accepted with { submissionId }
 *  3. Worker processes the submission asynchronously
 *  4. Worker pushes the final Submission to  /topic/submission/{submissionId}
 */
@Transactional
@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    /**
     * Create a new submission.
     * Returns 202 Accepted with the submission ID so the frontend can
     * immediately subscribe to /topic/submission/{id} for the async result.
     */
    @PostMapping
    public ResponseEntity<Map<String, Long>> submit(
            @RequestBody SubmissionRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        SubmissionCreatedDTO submission = submissionService.createSubmission(username, request);

        // Return only the ID — the result will arrive via WebSocket
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Map.of("submissionId", submission.getSubmissionId()));
    }

    @GetMapping("/my")
    public List<SubmissionResultDTO> getMySubmissions(Authentication authentication) {
        String username = authentication.getName();
        return submissionService.getUserHistory(username);
    }

    @GetMapping("/problem/{problemId}")
    public List<SubmissionResultDTO> getProblemSubmissions(
            @PathVariable Long problemId,
            Authentication authentication) {

        String username = authentication.getName();
        return submissionService.getProblemHistory(username, problemId);
    }

    @GetMapping("/problem/{problemId}/latest")
    public ResponseEntity<Map<String, String>> getLatestProblemSubmission(
            @PathVariable Long problemId,
            Authentication authentication) {

        String username = authentication.getName();
        String code = submissionService.getLatestProblemSubmission(username, problemId);
        if (code == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("code", code));
    }
}
