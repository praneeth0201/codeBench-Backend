package com.codeBench.demo.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Tracks a "Run" request — executing only the sample test cases for a problem.
 * Mirrors the Submission entity but is separate so it doesn't pollute the
 * submission history.
 */
@Entity
@Table(name = "run_records")
public class RunRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private Long problemId;

    @Column(columnDefinition = "TEXT")
    private String code;

    private String language;

    /** PENDING → RUNNING → COMPLETED | RUNTIME_ERROR */
    private String status;

    /** JSON-serialised list of per-sample results, stored as TEXT */
    @Column(columnDefinition = "TEXT")
    private String output;

    private Long executionTime;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId()                          { return id; }
    public String getUsername()                  { return username; }
    public void   setUsername(String username)   { this.username = username; }
    public Long   getProblemId()                 { return problemId; }
    public void   setProblemId(Long problemId)   { this.problemId = problemId; }
    public String getCode()                      { return code; }
    public void   setCode(String code)           { this.code = code; }
    public String getLanguage()                  { return language; }
    public void   setLanguage(String language)   { this.language = language; }
    public String getStatus()                    { return status; }
    public void   setStatus(String status)       { this.status = status; }
    public String getOutput()                    { return output; }
    public void   setOutput(String output)       { this.output = output; }
    public Long   getExecutionTime()             { return executionTime; }
    public void   setExecutionTime(Long t)       { this.executionTime = t; }
    public LocalDateTime getCreatedAt()          { return createdAt; }

    @Override
    public String toString() {
        return "RunRecord{id=" + id + ", username='" + username + "', status='" + status + "'}";
    }
}
