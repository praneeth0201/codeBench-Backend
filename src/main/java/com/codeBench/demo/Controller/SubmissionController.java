package com.codeBench.demo.Controller;

import com.codeBench.demo.DTO.SubmissionRequest;
import com.codeBench.demo.Entity.Submission;
import com.codeBench.demo.Services.SubmissionService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Transactional
@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping
    public ResponseEntity<?> submit(
            @RequestBody SubmissionRequest request,
            Authentication authentication
    ) {

        String username = authentication.getName();

        Submission submission =
                submissionService.createSubmission(username, request);
        System.out.println(submission.toString());
        return ResponseEntity.ok(submission);
    }

    @GetMapping("/my")
    public List<Submission> getMySubmissions(Authentication authentication) {
        String username = authentication.getName();
        System.out.println(username);
        List<Submission> x= submissionService.getUserHistory(username);
        System.out.println(x.toString());
        return x;
    }


    @GetMapping("/problem/{problemId}")
    public List<Submission> getProblemSubmissions(
            @PathVariable Long problemId,
            Authentication authentication) {

        String username = authentication.getName();
        return submissionService.getProblemHistory(username, problemId);
    }
}
