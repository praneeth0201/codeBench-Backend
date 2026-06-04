package com.codeBench.demo.DTO;

import com.codeBench.demo.Entity.SubmissionStatus;
import com.codeBench.demo.Entity.Verdict;

public class SubmissionResultDTO {
    private Long submissionId;
    private SubmissionStatus status;
    private Verdict verdict;
    private Long executionTime;
    private Integer passedTestCases;
    private Integer totalTestCases;
    private String failedInput;
    private String expectedOutput;
    private String actualOutput;
    private String compileError;

    public SubmissionResultDTO() {}

    public SubmissionResultDTO(Long submissionId, SubmissionStatus status, Verdict verdict, Long executionTime, Integer passedTestCases, Integer totalTestCases, String failedInput, String expectedOutput, String actualOutput, String compileError) {
        this.submissionId = submissionId;
        this.status = status;
        this.verdict = verdict;
        this.executionTime = executionTime;
        this.passedTestCases = passedTestCases;
        this.totalTestCases = totalTestCases;
        this.failedInput = failedInput;
        this.expectedOutput = expectedOutput;
        this.actualOutput = actualOutput;
        this.compileError = compileError;
    }

    public Long getSubmissionId() { return submissionId; }
    public void setSubmissionId(Long submissionId) { this.submissionId = submissionId; }
    public SubmissionStatus getStatus() { return status; }
    public void setStatus(SubmissionStatus status) { this.status = status; }
    public Verdict getVerdict() { return verdict; }
    public void setVerdict(Verdict verdict) { this.verdict = verdict; }
    public Long getExecutionTime() { return executionTime; }
    public void setExecutionTime(Long executionTime) { this.executionTime = executionTime; }
    public Integer getPassedTestCases() { return passedTestCases; }
    public void setPassedTestCases(Integer passedTestCases) { this.passedTestCases = passedTestCases; }
    public Integer getTotalTestCases() { return totalTestCases; }
    public void setTotalTestCases(Integer totalTestCases) { this.totalTestCases = totalTestCases; }
    public String getFailedInput() { return failedInput; }
    public void setFailedInput(String failedInput) { this.failedInput = failedInput; }
    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
    public String getActualOutput() { return actualOutput; }
    public void setActualOutput(String actualOutput) { this.actualOutput = actualOutput; }
    public String getCompileError() { return compileError; }
    public void setCompileError(String compileError) { this.compileError = compileError; }
}
