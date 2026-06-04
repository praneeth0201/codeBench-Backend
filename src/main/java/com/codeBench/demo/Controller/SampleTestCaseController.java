package com.codeBench.demo.Controller;

import com.codeBench.demo.DTO.SampleTestCaseRequest;
import com.codeBench.demo.DTO.SampleTestCaseResponse;
import com.codeBench.demo.Services.SampleTestCaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problems/{problemId}/samples")
public class SampleTestCaseController {

    private final SampleTestCaseService service;

    public SampleTestCaseController(SampleTestCaseService service) {
        this.service = service;
    }

    /** GET /api/problems/{problemId}/samples  — list all samples for a problem */
    @GetMapping
    public ResponseEntity<List<SampleTestCaseResponse>> getAll(@PathVariable Long problemId) {
        return ResponseEntity.ok(service.getAllForProblem(problemId));
    }

    /** GET /api/problems/{problemId}/samples/{id}  — get a single sample */
    @GetMapping("/{id}")
    public ResponseEntity<SampleTestCaseResponse> getOne(@PathVariable Long problemId,
                                                          @PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

}
