package com.codeBench.demo.DTO;

import com.codeBench.demo.Entity.Difficulty;

public class ProblemListResponse {

    private Long id;
    private String title;
    private String slug;
    private Difficulty difficulty;
    private Double acceptanceRate;
    private boolean solved;

    public ProblemListResponse() {}

    public ProblemListResponse(Long id, String title, String slug, Difficulty difficulty, Double acceptanceRate, boolean solved) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.difficulty = difficulty;
        this.acceptanceRate = acceptanceRate;
        this.solved = solved;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public Double getAcceptanceRate() { return acceptanceRate; }
    public void setAcceptanceRate(Double acceptanceRate) { this.acceptanceRate = acceptanceRate; }

    public boolean isSolved() { return solved; }
    public void setSolved(boolean solved) { this.solved = solved; }
}
