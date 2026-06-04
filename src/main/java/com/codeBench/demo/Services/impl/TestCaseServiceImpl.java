package com.codeBench.demo.Services.impl;

import com.codeBench.demo.DAO.ProblemRepository;
import com.codeBench.demo.DAO.TestCaseRepository;
import com.codeBench.demo.DTO.TestCaseRequest;
import com.codeBench.demo.DTO.TestCaseResponse;
import com.codeBench.demo.Entity.Problem;
import com.codeBench.demo.Entity.TestCase;
import com.codeBench.demo.Services.TestCaseService;
import com.codeBench.demo.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestCaseServiceImpl implements TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final ProblemRepository problemRepository;

    public TestCaseServiceImpl(TestCaseRepository testCaseRepository, ProblemRepository problemRepository) {
        this.testCaseRepository = testCaseRepository;
        this.problemRepository = problemRepository;
    }

    @Override
    @Transactional
    public List<TestCaseResponse> createMultipleTestCases(Long problemId, List<TestCaseRequest> requests) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));

        List<TestCase> testCases = requests.stream().map(req -> {
            TestCase testCase = new TestCase();
            testCase.setProblemId(problem.getId());
            testCase.setInput(req.getInput());
            testCase.setExpectedOutput(req.getExpectedOutput());
            return testCase;
        }).collect(Collectors.toList());

        return testCaseRepository.saveAll(testCases).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TestCaseResponse updateTestCase(Long id, TestCaseRequest request) {
        TestCase testCase = testCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestCase not found"));

        testCase.setInput(request.getInput());
        testCase.setExpectedOutput(request.getExpectedOutput());

        return toResponse(testCaseRepository.save(testCase));
    }

    @Override
    @Transactional
    public void deleteTestCase(Long id) {
        TestCase testCase = testCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestCase not found"));
        testCaseRepository.delete(testCase);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestCaseResponse> getAllForProblem(Long problemId) {
        return testCaseRepository.findByProblemId(problemId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TestCaseResponse toResponse(TestCase testCase) {
        return TestCaseResponse.builder()
                .id(testCase.getId())
                .problemId(testCase.getProblemId())
                .input(testCase.getInput())
                .expectedOutput(testCase.getExpectedOutput())
                .build();
    }
}
