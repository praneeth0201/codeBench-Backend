package com.codeBench.demo.DTO;

import java.util.List;

public class RunResultDTO {
    private Long runId;
    private String status;
    private String verdict;
    private Long executionTime;
    private List<SampleResultDTO> sampleResults;

    public RunResultDTO() {}

    public RunResultDTO(Long runId, String status, String verdict, Long executionTime, List<SampleResultDTO> sampleResults) {
        this.runId = runId;
        this.status = status;
        this.verdict = verdict;
        this.executionTime = executionTime;
        this.sampleResults = sampleResults;
    }

    public Long getRunId() {
        return runId;
    }

    public void setRunId(Long runId) {
        this.runId = runId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public List<SampleResultDTO> getSampleResults() {
        return sampleResults;
    }

    public void setSampleResults(List<SampleResultDTO> sampleResults) {
        this.sampleResults = sampleResults;
    }
}
