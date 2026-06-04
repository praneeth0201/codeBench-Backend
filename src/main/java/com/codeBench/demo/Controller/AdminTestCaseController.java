package com.codeBench.demo.Controller;

import com.codeBench.demo.DTO.TestCaseRequest;
import com.codeBench.demo.DTO.TestCaseResponse;
import com.codeBench.demo.Services.TestCaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTestCaseController {

    private final TestCaseService testCaseService;

    public AdminTestCaseController(TestCaseService testCaseService) {
        this.testCaseService = testCaseService;
    }

    @PostMapping("/problems/{problemId}/testcases")
    public ResponseEntity<List<TestCaseResponse>> createMultipleTestCases(@PathVariable Long problemId, @Valid @RequestBody List<TestCaseRequest> requests) {
        return new ResponseEntity<>(testCaseService.createMultipleTestCases(problemId, requests), HttpStatus.CREATED);
    }

    @PutMapping("/testcases/{id}")
    public ResponseEntity<TestCaseResponse> updateTestCase(@PathVariable Long id, @Valid @RequestBody TestCaseRequest request) {
        return ResponseEntity.ok(testCaseService.updateTestCase(id, request));
    }

    @DeleteMapping("/testcases/{id}")
    public ResponseEntity<Void> deleteTestCase(@PathVariable Long id) {
        testCaseService.deleteTestCase(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/problems/{problemId}/testcases")
    public ResponseEntity<List<TestCaseResponse>> getAllForProblem(@PathVariable Long problemId) {
        return ResponseEntity.ok(testCaseService.getAllForProblem(problemId));
    }
}
