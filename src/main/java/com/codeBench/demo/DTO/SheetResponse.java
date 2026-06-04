package com.codeBench.demo.DTO;

import java.time.LocalDateTime;

public class SheetResponse {

    private Long id;
    private String title;
    private String slug;
    private String description;
    private Boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SheetResponse() {}

    public SheetResponse(Long id, String title, String slug, String description, Boolean published, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.published = published;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static SheetResponseBuilder builder() { return new SheetResponseBuilder(); }

    public static class SheetResponseBuilder {
        private Long id;
        private String title;
        private String slug;
        private String description;
        private Boolean published;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public SheetResponseBuilder id(Long id) { this.id = id; return this; }
        public SheetResponseBuilder title(String title) { this.title = title; return this; }
        public SheetResponseBuilder slug(String slug) { this.slug = slug; return this; }
        public SheetResponseBuilder description(String description) { this.description = description; return this; }
        public SheetResponseBuilder published(Boolean published) { this.published = published; return this; }
        public SheetResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public SheetResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public SheetResponse build() { return new SheetResponse(id, title, slug, description, published, createdAt, updatedAt); }
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public Boolean getPublished() { return published; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
