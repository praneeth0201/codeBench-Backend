package com.codeBench.demo.DTO;

import com.codeBench.demo.Entity.Difficulty;
import jakarta.validation.constraints.*;
import java.util.List;

public class ProblemRequest {

    @NotBlank
    @Size(max = 300)
    private String title;

    @NotBlank
    private String statement;

    private String inputFormat;
    private String outputFormat;
    private String constraints;

    @NotNull
    private Difficulty difficulty;

    @NotNull
    @Positive
    private Integer timeLimit;

    @NotNull
    @Positive
    private Integer memoryLimit;

    private Boolean published = false;

    private List<SampleTestCaseRequest> sampleTestCases;

    private List<TestCaseRequest> testCases;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getStatement() { return statement; }
    public void setStatement(String statement) { this.statement = statement; }
    public String getInputFormat() { return inputFormat; }
    public void setInputFormat(String inputFormat) { this.inputFormat = inputFormat; }
    public String getOutputFormat() { return outputFormat; }
    public void setOutputFormat(String outputFormat) { this.outputFormat = outputFormat; }
    public String getConstraints() { return constraints; }
    public void setConstraints(String constraints) { this.constraints = constraints; }
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    public Integer getTimeLimit() { return timeLimit; }
    public void setTimeLimit(Integer timeLimit) { this.timeLimit = timeLimit; }
    public Integer getMemoryLimit() { return memoryLimit; }
    public void setMemoryLimit(Integer memoryLimit) { this.memoryLimit = memoryLimit; }
    public Boolean getPublished() { return published; }
    public void setPublished(Boolean published) { this.published = published; }
    public List<SampleTestCaseRequest> getSampleTestCases() { return sampleTestCases; }
    public void setSampleTestCases(List<SampleTestCaseRequest> sampleTestCases) { this.sampleTestCases = sampleTestCases; }
    public List<TestCaseRequest> getTestCases() { return testCases; }
    public void setTestCases(List<TestCaseRequest> testCases) { this.testCases = testCases; }
}
