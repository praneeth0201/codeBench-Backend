package com.codeBench.demo.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private Long problemId;

    @Column(columnDefinition = "TEXT")
    private String code;

    private String language;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;

    @Enumerated(EnumType.STRING)
    private Verdict verdict;

    private Integer passedTestCases;
    private Integer totalTestCases;
    private Long memoryUsed;

    @Column(columnDefinition = "TEXT")
    private String failedInput;

    @Column(columnDefinition = "TEXT")
    private String expectedOutput;

    @Column(columnDefinition = "TEXT")
    private String actualOutput;

    @Column(columnDefinition = "TEXT")
    private String compileError;

    @Column(columnDefinition = "TEXT")
    private String output;

    private Long executionTime;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Long getProblemId() { return problemId; }
    public void setProblemId(Long problemId) { this.problemId = problemId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public SubmissionStatus getStatus() { return status; }
    public void setStatus(SubmissionStatus status) { this.status = status; }
    public Verdict getVerdict() { return verdict; }
    public void setVerdict(Verdict verdict) { this.verdict = verdict; }
    public Integer getPassedTestCases() { return passedTestCases; }
    public void setPassedTestCases(Integer passedTestCases) { this.passedTestCases = passedTestCases; }
    public Integer getTotalTestCases() { return totalTestCases; }
    public void setTotalTestCases(Integer totalTestCases) { this.totalTestCases = totalTestCases; }
    public Long getMemoryUsed() { return memoryUsed; }
    public void setMemoryUsed(Long memoryUsed) { this.memoryUsed = memoryUsed; }
    public String getFailedInput() { return failedInput; }
    public void setFailedInput(String failedInput) { this.failedInput = failedInput; }
    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
    public String getActualOutput() { return actualOutput; }
    public void setActualOutput(String actualOutput) { this.actualOutput = actualOutput; }
    public String getCompileError() { return compileError; }
    public void setCompileError(String compileError) { this.compileError = compileError; }
    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }
    public Long getExecutionTime() { return executionTime; }
    public void setExecutionTime(Long executionTime) { this.executionTime = executionTime; }

    @Override
    public String toString() {
        return "Submission{id=" + id + ", username='" + username + "', status='" + status + "'}";
    }
}
