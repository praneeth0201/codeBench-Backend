package com.codeBench.demo.Services;

import com.codeBench.demo.DTO.TestCaseRequest;
import com.codeBench.demo.DTO.TestCaseResponse;

import java.util.List;

public interface TestCaseService {

    List<TestCaseResponse> createMultipleTestCases(Long problemId, List<TestCaseRequest> requests);
    TestCaseResponse updateTestCase(Long id, TestCaseRequest request);
    void deleteTestCase(Long id);

    List<TestCaseResponse> getAllForProblem(Long problemId);
}
