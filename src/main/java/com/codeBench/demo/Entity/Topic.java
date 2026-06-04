package com.codeBench.demo.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(
    name = "topics",
    indexes = {
        @Index(name = "idx_topic_slug", columnList = "slug", unique = true),
        @Index(name = "idx_topic_name", columnList = "name")
    }
)
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProblemTopic> problemTopics = new ArrayList<>();

    public Topic() {}

    public Topic(Long id, String name, String slug, String description, LocalDateTime createdAt, List<ProblemTopic> problemTopics) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.createdAt = createdAt;
        this.problemTopics = problemTopics != null ? problemTopics : new ArrayList<>();
    }

    public static TopicBuilder builder() { return new TopicBuilder(); }

    public static class TopicBuilder {
        private Long id;
        private String name;
        private String slug;
        private String description;
        private LocalDateTime createdAt;
        private List<ProblemTopic> problemTopics;

        public TopicBuilder id(Long id) { this.id = id; return this; }
        public TopicBuilder name(String name) { this.name = name; return this; }
        public TopicBuilder slug(String slug) { this.slug = slug; return this; }
        public TopicBuilder description(String description) { this.description = description; return this; }
        public TopicBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public TopicBuilder problemTopics(List<ProblemTopic> problemTopics) { this.problemTopics = problemTopics; return this; }
        public Topic build() { return new Topic(id, name, slug, description, createdAt, problemTopics); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<ProblemTopic> getProblemTopics() { return problemTopics; }
    public void setProblemTopics(List<ProblemTopic> problemTopics) { this.problemTopics = problemTopics; }
}
