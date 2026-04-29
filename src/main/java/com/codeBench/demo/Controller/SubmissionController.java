package com.codeBench.demo.Controller;

import com.codeBench.demo.DTO.SubmissionRequest;
import com.codeBench.demo.Entity.Submission;
import com.codeBench.demo.Services.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
