package com.codeBench.demo.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SampleTestCaseRequest {

    @NotNull
    @Min(1)
    private Integer orderIndex;

    @NotBlank
    private String input;

    @NotBlank
    private String expectedOutput;

    private String description;

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
