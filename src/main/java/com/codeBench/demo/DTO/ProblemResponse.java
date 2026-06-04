package com.codeBench.demo.DTO;

import com.codeBench.demo.Entity.Difficulty;
import java.time.LocalDateTime;
import java.util.List;

public class ProblemResponse {

    private Long id;
    private String title;
    private String slug;
    private String statement;
    private String inputFormat;
    private String outputFormat;
    private String constraints;
    private Difficulty difficulty;
    private Integer timeLimit;
    private Integer memoryLimit;
    private Double acceptanceRate;
    private List<SampleTestCaseResponse> sampleTestCases;

    public ProblemResponse() {}

    public ProblemResponse(Long id, String title, String slug, String statement, String inputFormat, String outputFormat, String constraints, Difficulty difficulty, Integer timeLimit, Integer memoryLimit, Double acceptanceRate, List<SampleTestCaseResponse> sampleTestCases) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.statement = statement;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.constraints = constraints;
        this.difficulty = difficulty;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        this.acceptanceRate = acceptanceRate;
        this.sampleTestCases = sampleTestCases;
    }

    public static ProblemResponseBuilder builder() { return new ProblemResponseBuilder(); }

    public static class ProblemResponseBuilder {
        private Long id;
        private String title;
        private String slug;
        private String statement;
        private String inputFormat;
        private String outputFormat;
        private String constraints;
        private Difficulty difficulty;
        private Integer timeLimit;
        private Integer memoryLimit;
        private Double acceptanceRate;
        private List<SampleTestCaseResponse> sampleTestCases;

        public ProblemResponseBuilder id(Long id) { this.id = id; return this; }
        public ProblemResponseBuilder title(String title) { this.title = title; return this; }
        public ProblemResponseBuilder slug(String slug) { this.slug = slug; return this; }
        public ProblemResponseBuilder statement(String statement) { this.statement = statement; return this; }
        public ProblemResponseBuilder inputFormat(String inputFormat) { this.inputFormat = inputFormat; return this; }
        public ProblemResponseBuilder outputFormat(String outputFormat) { this.outputFormat = outputFormat; return this; }
        public ProblemResponseBuilder constraints(String constraints) { this.constraints = constraints; return this; }
        public ProblemResponseBuilder difficulty(Difficulty difficulty) { this.difficulty = difficulty; return this; }
        public ProblemResponseBuilder timeLimit(Integer timeLimit) { this.timeLimit = timeLimit; return this; }
        public ProblemResponseBuilder memoryLimit(Integer memoryLimit) { this.memoryLimit = memoryLimit; return this; }
        public ProblemResponseBuilder acceptanceRate(Double acceptanceRate) { this.acceptanceRate = acceptanceRate; return this; }
        public ProblemResponseBuilder sampleTestCases(List<SampleTestCaseResponse> sampleTestCases) { this.sampleTestCases = sampleTestCases; return this; }
        public ProblemResponse build() { return new ProblemResponse(id, title, slug, statement, inputFormat, outputFormat, constraints, difficulty, timeLimit, memoryLimit, acceptanceRate, sampleTestCases); }
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getSlug() { return slug; }
    public String getStatement() { return statement; }
    public String getInputFormat() { return inputFormat; }
    public String getOutputFormat() { return outputFormat; }
    public String getConstraints() { return constraints; }
    public Difficulty getDifficulty() { return difficulty; }
    public Integer getTimeLimit() { return timeLimit; }
    public Integer getMemoryLimit() { return memoryLimit; }
    public Double getAcceptanceRate() { return acceptanceRate; }
    public List<SampleTestCaseResponse> getSampleTestCases() { return sampleTestCases; }
}
