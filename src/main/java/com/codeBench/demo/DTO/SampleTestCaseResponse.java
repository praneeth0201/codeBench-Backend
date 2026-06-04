package com.codeBench.demo.DTO;

public class SampleTestCaseResponse {

    private Long id;
    private Long problemId;
    private Integer orderIndex;
    private String input;
    private String expectedOutput;
    private String description;

    public SampleTestCaseResponse() {}

    public SampleTestCaseResponse(Long id, Long problemId, Integer orderIndex, String input, String expectedOutput, String description) {
        this.id = id;
        this.problemId = problemId;
        this.orderIndex = orderIndex;
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.description = description;
    }

    public static SampleTestCaseResponseBuilder builder() { return new SampleTestCaseResponseBuilder(); }

    public static class SampleTestCaseResponseBuilder {
        private Long id;
        private Long problemId;
        private Integer orderIndex;
        private String input;
        private String expectedOutput;
        private String description;

        public SampleTestCaseResponseBuilder id(Long id) { this.id = id; return this; }
        public SampleTestCaseResponseBuilder problemId(Long problemId) { this.problemId = problemId; return this; }
        public SampleTestCaseResponseBuilder orderIndex(Integer orderIndex) { this.orderIndex = orderIndex; return this; }
        public SampleTestCaseResponseBuilder input(String input) { this.input = input; return this; }
        public SampleTestCaseResponseBuilder expectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; return this; }
        public SampleTestCaseResponseBuilder description(String description) { this.description = description; return this; }
        public SampleTestCaseResponse build() { return new SampleTestCaseResponse(id, problemId, orderIndex, input, expectedOutput, description); }
    }

    public Long getId() { return id; }
    public Long getProblemId() { return problemId; }
    public Integer getOrderIndex() { return orderIndex; }
    public String getInput() { return input; }
    public String getExpectedOutput() { return expectedOutput; }
    public String getDescription() { return description; }
}
