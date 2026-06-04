package com.codeBench.demo.DTO;

import java.time.LocalDateTime;

public class TopicResponse {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private LocalDateTime createdAt;

    public TopicResponse() {}

    public TopicResponse(Long id, String name, String slug, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static TopicResponseBuilder builder() { return new TopicResponseBuilder(); }

    public static class TopicResponseBuilder {
        private Long id;
        private String name;
        private String slug;
        private String description;
        private LocalDateTime createdAt;

        public TopicResponseBuilder id(Long id) { this.id = id; return this; }
        public TopicResponseBuilder name(String name) { this.name = name; return this; }
        public TopicResponseBuilder slug(String slug) { this.slug = slug; return this; }
        public TopicResponseBuilder description(String description) { this.description = description; return this; }
        public TopicResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public TopicResponse build() { return new TopicResponse(id, name, slug, description, createdAt); }
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
