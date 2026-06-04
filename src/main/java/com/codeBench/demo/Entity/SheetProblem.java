package com.codeBench.demo.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "sheet_problems",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_sheet_problem", columnNames = {"sheet_id", "problem_id"})
    },
    indexes = {
        @Index(name = "idx_sp_sheet",   columnList = "sheet_id"),
        @Index(name = "idx_sp_problem", columnList = "problem_id"),
        @Index(name = "idx_sp_order",   columnList = "sheet_id, order_index")
    }
)
public class SheetProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sheet_id", nullable = false)
    private Sheet sheet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @CreationTimestamp
    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;

    public SheetProblem() {}

    public SheetProblem(Long id, Sheet sheet, Problem problem, Integer orderIndex, LocalDateTime addedAt) {
        this.id = id;
        this.sheet = sheet;
        this.problem = problem;
        this.orderIndex = orderIndex;
        this.addedAt = addedAt;
    }

    public static SheetProblemBuilder builder() { return new SheetProblemBuilder(); }

    public static class SheetProblemBuilder {
        private Long id;
        private Sheet sheet;
        private Problem problem;
        private Integer orderIndex;
        private LocalDateTime addedAt;

        public SheetProblemBuilder id(Long id) { this.id = id; return this; }
        public SheetProblemBuilder sheet(Sheet sheet) { this.sheet = sheet; return this; }
        public SheetProblemBuilder problem(Problem problem) { this.problem = problem; return this; }
        public SheetProblemBuilder orderIndex(Integer orderIndex) { this.orderIndex = orderIndex; return this; }
        public SheetProblemBuilder addedAt(LocalDateTime addedAt) { this.addedAt = addedAt; return this; }
        public SheetProblem build() { return new SheetProblem(id, sheet, problem, orderIndex, addedAt); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Sheet getSheet() { return sheet; }
    public void setSheet(Sheet sheet) { this.sheet = sheet; }
    public Problem getProblem() { return problem; }
    public void setProblem(Problem problem) { this.problem = problem; }
    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}
