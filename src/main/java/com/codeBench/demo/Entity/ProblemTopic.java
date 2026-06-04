package com.codeBench.demo.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "problem_topics",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_problem_topic", columnNames = {"problem_id", "topic_id"})
    },
    indexes = {
        @Index(name = "idx_pt_problem", columnList = "problem_id"),
        @Index(name = "idx_pt_topic",   columnList = "topic_id")
    }
)
public class ProblemTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ProblemTopic() {}

    public ProblemTopic(Long id, Problem problem, Topic topic, LocalDateTime createdAt) {
        this.id = id;
        this.problem = problem;
        this.topic = topic;
        this.createdAt = createdAt;
    }

    public static ProblemTopicBuilder builder() { return new ProblemTopicBuilder(); }

    public static class ProblemTopicBuilder {
        private Long id;
        private Problem problem;
        private Topic topic;
        private LocalDateTime createdAt;

        public ProblemTopicBuilder id(Long id) { this.id = id; return this; }
        public ProblemTopicBuilder problem(Problem problem) { this.problem = problem; return this; }
        public ProblemTopicBuilder topic(Topic topic) { this.topic = topic; return this; }
        public ProblemTopicBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ProblemTopic build() { return new ProblemTopic(id, problem, topic, createdAt); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Problem getProblem() { return problem; }
    public void setProblem(Problem problem) { this.problem = problem; }
    public Topic getTopic() { return topic; }
    public void setTopic(Topic topic) { this.topic = topic; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
