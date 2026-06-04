package com.codeBench.demo.Controller;

import com.codeBench.demo.DTO.ProblemRequest;
import com.codeBench.demo.DTO.ProblemResponse;
import com.codeBench.demo.Services.ProblemService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/problems")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProblemController {

    private final ProblemService problemService;

    public AdminProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @PostMapping
    public ResponseEntity<ProblemResponse> createProblem(@Valid @RequestBody ProblemRequest request) {
        return new ResponseEntity<>(problemService.createProblem(request), HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<ProblemResponse>> createMultipleProblems(@RequestBody List<ProblemRequest> requests) {
        return new ResponseEntity<>(problemService.createMultipleProblems(requests), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProblemResponse> updateProblem(@PathVariable Long id, @Valid @RequestBody ProblemRequest request) {
        return ResponseEntity.ok(problemService.updateProblem(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<Void> publishProblem(@PathVariable Long id) {
        problemService.publishProblem(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/unpublish")
    public ResponseEntity<Void> unpublishProblem(@PathVariable Long id) {
        problemService.unpublishProblem(id);
        return ResponseEntity.ok().build();
    }
}
