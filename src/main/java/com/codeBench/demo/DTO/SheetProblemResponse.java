package com.codeBench.demo.DTO;

import com.codeBench.demo.Entity.Difficulty;
import java.time.LocalDateTime;

public class SheetProblemResponse {

    private Long id;
    private Long sheetId;
    private Long problemId;
    private String problemTitle;
    private String problemSlug;
    private Difficulty difficulty;
    private Integer orderIndex;
    private LocalDateTime addedAt;

    public SheetProblemResponse() {}

    public SheetProblemResponse(Long id, Long sheetId, Long problemId, String problemTitle, String problemSlug, Difficulty difficulty, Integer orderIndex, LocalDateTime addedAt) {
        this.id = id;
        this.sheetId = sheetId;
        this.problemId = problemId;
        this.problemTitle = problemTitle;
        this.problemSlug = problemSlug;
        this.difficulty = difficulty;
        this.orderIndex = orderIndex;
        this.addedAt = addedAt;
    }

    public static SheetProblemResponseBuilder builder() { return new SheetProblemResponseBuilder(); }

    public static class SheetProblemResponseBuilder {
        private Long id;
        private Long sheetId;
        private Long problemId;
        private String problemTitle;
        private String problemSlug;
        private Difficulty difficulty;
        private Integer orderIndex;
        private LocalDateTime addedAt;

        public SheetProblemResponseBuilder id(Long id) { this.id = id; return this; }
        public SheetProblemResponseBuilder sheetId(Long sheetId) { this.sheetId = sheetId; return this; }
        public SheetProblemResponseBuilder problemId(Long problemId) { this.problemId = problemId; return this; }
        public SheetProblemResponseBuilder problemTitle(String problemTitle) { this.problemTitle = problemTitle; return this; }
        public SheetProblemResponseBuilder problemSlug(String problemSlug) { this.problemSlug = problemSlug; return this; }
        public SheetProblemResponseBuilder difficulty(Difficulty difficulty) { this.difficulty = difficulty; return this; }
        public SheetProblemResponseBuilder orderIndex(Integer orderIndex) { this.orderIndex = orderIndex; return this; }
        public SheetProblemResponseBuilder addedAt(LocalDateTime addedAt) { this.addedAt = addedAt; return this; }
        public SheetProblemResponse build() { return new SheetProblemResponse(id, sheetId, problemId, problemTitle, problemSlug, difficulty, orderIndex, addedAt); }
    }

    public Long getId() { return id; }
    public Long getSheetId() { return sheetId; }
    public Long getProblemId() { return problemId; }
    public String getProblemTitle() { return problemTitle; }
    public String getProblemSlug() { return problemSlug; }
    public Difficulty getDifficulty() { return difficulty; }
    public Integer getOrderIndex() { return orderIndex; }
    public LocalDateTime getAddedAt() { return addedAt; }
}
