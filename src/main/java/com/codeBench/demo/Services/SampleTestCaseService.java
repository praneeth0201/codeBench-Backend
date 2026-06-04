package com.codeBench.demo.Services;

import com.codeBench.demo.DAO.ProblemRepository;
import com.codeBench.demo.DAO.SampleTestCaseRepository;
import com.codeBench.demo.DTO.SampleTestCaseRequest;
import com.codeBench.demo.DTO.SampleTestCaseResponse;
import com.codeBench.demo.Entity.Problem;
import com.codeBench.demo.Entity.SampleTestCase;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SampleTestCaseService {

    private final SampleTestCaseRepository sampleTestCaseRepository;
    private final ProblemRepository problemRepository;

    public SampleTestCaseService(SampleTestCaseRepository sampleTestCaseRepository,
                                 ProblemRepository problemRepository) {
        this.sampleTestCaseRepository = sampleTestCaseRepository;
        this.problemRepository = problemRepository;
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    public List<SampleTestCaseResponse> createMultiple(Long problemId, List<SampleTestCaseRequest> requests) {
        Problem problem = findProblemOrThrow(problemId);

        List<SampleTestCase> samples = requests.stream().map(req -> SampleTestCase.builder()
                .problem(problem)
                .orderIndex(req.getOrderIndex())
                .input(req.getInput())
                .expectedOutput(req.getExpectedOutput())
                .description(req.getDescription())
                .build()).collect(Collectors.toList());

        List<SampleTestCase> savedSamples = sampleTestCaseRepository.saveAll(samples);

        return savedSamples.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public List<SampleTestCaseResponse> getAllForProblem(Long problemId) {
        findProblemOrThrow(problemId);
        return sampleTestCaseRepository
                .findByProblemIdOrderByOrderIndexAsc(problemId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public SampleTestCaseResponse getById(Long id) {
        return toResponse(findSampleOrThrow(id));
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public SampleTestCaseResponse update(Long id, SampleTestCaseRequest req) {
        SampleTestCase sample = findSampleOrThrow(id);

        sample.setOrderIndex(req.getOrderIndex());
        sample.setInput(req.getInput());
        sample.setExpectedOutput(req.getExpectedOutput());
        sample.setDescription(req.getDescription());

        return toResponse(sampleTestCaseRepository.save(sample));
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public void delete(Long id) {
        findSampleOrThrow(id);
        sampleTestCaseRepository.deleteById(id);
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private Problem findProblemOrThrow(Long problemId) {
        return problemRepository.findById(problemId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Problem not found: " + problemId));
    }

    private SampleTestCase findSampleOrThrow(Long id) {
        return sampleTestCaseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "SampleTestCase not found: " + id));
    }

    private SampleTestCaseResponse toResponse(SampleTestCase s) {
        return SampleTestCaseResponse.builder()
                .id(s.getId())
                .problemId(s.getProblem().getId())
                .orderIndex(s.getOrderIndex())
                .input(s.getInput())
                .expectedOutput(s.getExpectedOutput())
                .description(s.getDescription())
                .build();
    }
}
