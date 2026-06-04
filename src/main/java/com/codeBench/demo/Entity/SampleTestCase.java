package com.codeBench.demo.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sample_test_cases")
public class SampleTestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(nullable = false)
    private Integer orderIndex;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String input;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String expectedOutput;

    @Column(columnDefinition = "TEXT")
    private String description;

    public SampleTestCase() {}

    public SampleTestCase(Long id, Problem problem, Integer orderIndex, String input, String expectedOutput, String description) {
        this.id = id;
        this.problem = problem;
        this.orderIndex = orderIndex;
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.description = description;
    }

    public static SampleTestCaseBuilder builder() { return new SampleTestCaseBuilder(); }

    public static class SampleTestCaseBuilder {
        private Long id;
        private Problem problem;
        private Integer orderIndex;
        private String input;
        private String expectedOutput;
        private String description;

        public SampleTestCaseBuilder id(Long id) { this.id = id; return this; }
        public SampleTestCaseBuilder problem(Problem problem) { this.problem = problem; return this; }
        public SampleTestCaseBuilder orderIndex(Integer orderIndex) { this.orderIndex = orderIndex; return this; }
        public SampleTestCaseBuilder input(String input) { this.input = input; return this; }
        public SampleTestCaseBuilder expectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; return this; }
        public SampleTestCaseBuilder description(String description) { this.description = description; return this; }
        public SampleTestCase build() { return new SampleTestCase(id, problem, orderIndex, input, expectedOutput, description); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Problem getProblem() { return problem; }
    public void setProblem(Problem problem) { this.problem = problem; }
    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public String getExpectedOutput() { return expectedOutput; }
    public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
