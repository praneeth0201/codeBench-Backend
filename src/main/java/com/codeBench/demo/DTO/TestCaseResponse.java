package com.codeBench.demo.DTO;

public class TestCaseResponse {

    private Long id;
    private Long problemId;
    private String input;
    private String expectedOutput;

    public TestCaseResponse() {}
    public TestCaseResponse(Long id, Long problemId, String input, String expectedOutput) {
        this.id = id;
        this.problemId = problemId;
        this.input = input;
        this.expectedOutput = expectedOutput;
    }

    public static TestCaseResponseBuilder builder() { return new TestCaseResponseBuilder(); }

    public static class TestCaseResponseBuilder {
        private Long id;
        private Long problemId;
        private String input;
        private String expectedOutput;
        public TestCaseResponseBuilder id(Long id) { this.id = id; return this; }
        public TestCaseResponseBuilder problemId(Long problemId) { this.problemId = problemId; return this; }
        public TestCaseResponseBuilder input(String input) { this.input = input; return this; }
        public TestCaseResponseBuilder expectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; return this; }
        public TestCaseResponse build() { return new TestCaseResponse(id, problemId, input, expectedOutput); }
    }

    public Long getId() { return id; }
    public Long getProblemId() { return problemId; }
    public String getInput() { return input; }
    public String getExpectedOutput() { return expectedOutput; }
}
