package com.codeBench.demo.Controller;

import com.codeBench.demo.DTO.SampleTestCaseRequest;
import com.codeBench.demo.DTO.SampleTestCaseResponse;
import com.codeBench.demo.Services.SampleTestCaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSampleTestCaseController {

    private final SampleTestCaseService service;

    public AdminSampleTestCaseController(SampleTestCaseService service) {
        this.service = service;
    }

    @PostMapping("/problems/{problemId}/samples")
    public ResponseEntity<List<SampleTestCaseResponse>> createMultiple(@PathVariable Long problemId,
                                                          @Valid @RequestBody List<SampleTestCaseRequest> reqs) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createMultiple(problemId, reqs));
    }

    @PutMapping("/samples/{id}")
    public ResponseEntity<SampleTestCaseResponse> update(@PathVariable Long id,
                                                         @Valid @RequestBody SampleTestCaseRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/samples/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
