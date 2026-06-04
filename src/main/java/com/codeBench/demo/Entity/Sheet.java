package com.codeBench.demo.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "sheets",
    indexes = {
        @Index(name = "idx_sheet_slug",      columnList = "slug",      unique = true),
        @Index(name = "idx_sheet_title",     columnList = "title"),
        @Index(name = "idx_sheet_published", columnList = "published")
    }
)
public class Sheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, unique = true, length = 300)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean published = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "sheet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SheetProblem> sheetProblems = new ArrayList<>();

    public Sheet() {}

    public Sheet(Long id, String title, String slug, String description, Boolean published, LocalDateTime createdAt, LocalDateTime updatedAt, List<SheetProblem> sheetProblems) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.published = published != null ? published : false;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.sheetProblems = sheetProblems != null ? sheetProblems : new ArrayList<>();
    }

    public static SheetBuilder builder() { return new SheetBuilder(); }

    public static class SheetBuilder {
        private Long id;
        private String title;
        private String slug;
        private String description;
        private Boolean published = false;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<SheetProblem> sheetProblems;

        public SheetBuilder id(Long id) { this.id = id; return this; }
        public SheetBuilder title(String title) { this.title = title; return this; }
        public SheetBuilder slug(String slug) { this.slug = slug; return this; }
        public SheetBuilder description(String description) { this.description = description; return this; }
        public SheetBuilder published(Boolean published) { this.published = published; return this; }
        public SheetBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public SheetBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public SheetBuilder sheetProblems(List<SheetProblem> sheetProblems) { this.sheetProblems = sheetProblems; return this; }
        public Sheet build() { return new Sheet(id, title, slug, description, published, createdAt, updatedAt, sheetProblems); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getPublished() { return published; }
    public void setPublished(Boolean published) { this.published = published; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<SheetProblem> getSheetProblems() { return sheetProblems; }
    public void setSheetProblems(List<SheetProblem> sheetProblems) { this.sheetProblems = sheetProblems; }
}
