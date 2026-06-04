package com.codeBench.demo.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "problems",
    indexes = {
        @Index(name = "idx_problem_slug",       columnList = "slug",       unique = true),
        @Index(name = "idx_problem_title",      columnList = "title"),
        @Index(name = "idx_problem_difficulty", columnList = "difficulty"),
        @Index(name = "idx_problem_published",  columnList = "published")
    }
)
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, unique = true, length = 300)
    private String slug;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String statement;

    @Column(columnDefinition = "TEXT")
    private String inputFormat;

    @Column(columnDefinition = "TEXT")
    private String outputFormat;

    @Column(columnDefinition = "TEXT")
    private String constraints;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Difficulty difficulty;

    @Column(nullable = false)
    private Integer timeLimit;

    @Column(nullable = false)
    private Integer memoryLimit;

    @Column(nullable = false)
    private Double acceptanceRate = 0.0;

    @Column(nullable = false)
    private Long totalSubmissions = 0L;

    @Column(nullable = false)
    private Long totalAccepted = 0L;

    @Column(nullable = false)
    private Boolean published = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProblemTopic> problemTopics = new ArrayList<>();

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SheetProblem> sheetProblems = new ArrayList<>();

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SampleTestCase> sampleTestCases = new ArrayList<>();

    public Problem() {}

    public Problem(Long id, String title, String slug, String statement, String inputFormat, String outputFormat, String constraints, Difficulty difficulty, Integer timeLimit, Integer memoryLimit, Double acceptanceRate, Long totalSubmissions, Long totalAccepted, Boolean published, LocalDateTime createdAt, LocalDateTime updatedAt, List<ProblemTopic> problemTopics, List<SheetProblem> sheetProblems, List<SampleTestCase> sampleTestCases) {
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
        this.acceptanceRate = acceptanceRate != null ? acceptanceRate : 0.0;
        this.totalSubmissions = totalSubmissions != null ? totalSubmissions : 0L;
        this.totalAccepted = totalAccepted != null ? totalAccepted : 0L;
        this.published = published != null ? published : false;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.problemTopics = problemTopics != null ? problemTopics : new ArrayList<>();
        this.sheetProblems = sheetProblems != null ? sheetProblems : new ArrayList<>();
        this.sampleTestCases = sampleTestCases != null ? sampleTestCases : new ArrayList<>();
    }

    public static ProblemBuilder builder() { return new ProblemBuilder(); }

    public static class ProblemBuilder {
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
        private Double acceptanceRate = 0.0;
        private Long totalSubmissions = 0L;
        private Long totalAccepted = 0L;
        private Boolean published = false;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<ProblemTopic> problemTopics;
        private List<SheetProblem> sheetProblems;
        private List<SampleTestCase> sampleTestCases;

        public ProblemBuilder id(Long id) { this.id = id; return this; }
        public ProblemBuilder title(String title) { this.title = title; return this; }
        public ProblemBuilder slug(String slug) { this.slug = slug; return this; }
        public ProblemBuilder statement(String statement) { this.statement = statement; return this; }
        public ProblemBuilder inputFormat(String inputFormat) { this.inputFormat = inputFormat; return this; }
        public ProblemBuilder outputFormat(String outputFormat) { this.outputFormat = outputFormat; return this; }
        public ProblemBuilder constraints(String constraints) { this.constraints = constraints; return this; }
        public ProblemBuilder difficulty(Difficulty difficulty) { this.difficulty = difficulty; return this; }
        public ProblemBuilder timeLimit(Integer timeLimit) { this.timeLimit = timeLimit; return this; }
        public ProblemBuilder memoryLimit(Integer memoryLimit) { this.memoryLimit = memoryLimit; return this; }
        public ProblemBuilder acceptanceRate(Double acceptanceRate) { this.acceptanceRate = acceptanceRate; return this; }
        public ProblemBuilder totalSubmissions(Long totalSubmissions) { this.totalSubmissions = totalSubmissions; return this; }
        public ProblemBuilder totalAccepted(Long totalAccepted) { this.totalAccepted = totalAccepted; return this; }
        public ProblemBuilder published(Boolean published) { this.published = published; return this; }
        public ProblemBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ProblemBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public ProblemBuilder problemTopics(List<ProblemTopic> problemTopics) { this.problemTopics = problemTopics; return this; }
        public ProblemBuilder sheetProblems(List<SheetProblem> sheetProblems) { this.sheetProblems = sheetProblems; return this; }
        public ProblemBuilder sampleTestCases(List<SampleTestCase> sampleTestCases) { this.sampleTestCases = sampleTestCases; return this; }
        public Problem build() { return new Problem(id, title, slug, statement, inputFormat, outputFormat, constraints, difficulty, timeLimit, memoryLimit, acceptanceRate, totalSubmissions, totalAccepted, published, createdAt, updatedAt, problemTopics, sheetProblems, sampleTestCases); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
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
    public Double getAcceptanceRate() { return acceptanceRate; }
    public void setAcceptanceRate(Double acceptanceRate) { this.acceptanceRate = acceptanceRate; }
    public Long getTotalSubmissions() { return totalSubmissions; }
    public void setTotalSubmissions(Long totalSubmissions) { this.totalSubmissions = totalSubmissions; }
    public Long getTotalAccepted() { return totalAccepted; }
    public void setTotalAccepted(Long totalAccepted) { this.totalAccepted = totalAccepted; }
    public Boolean getPublished() { return published; }
    public void setPublished(Boolean published) { this.published = published; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<ProblemTopic> getProblemTopics() { return problemTopics; }
    public void setProblemTopics(List<ProblemTopic> problemTopics) { this.problemTopics = problemTopics; }
    public List<SheetProblem> getSheetProblems() { return sheetProblems; }
    public void setSheetProblems(List<SheetProblem> sheetProblems) { this.sheetProblems = sheetProblems; }
    public List<SampleTestCase> getSampleTestCases() { return sampleTestCases; }
    public void setSampleTestCases(List<SampleTestCase> sampleTestCases) { this.sampleTestCases = sampleTestCases; }
}
