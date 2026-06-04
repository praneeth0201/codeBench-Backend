package com.codeBench.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Payload the frontend sends when the user clicks "Run".
 * Mirrors SubmissionRequest but is for sample-only execution.
 */
public class RunRequest {

    @NotNull
    private Long problemId;

    @NotBlank
    private String code;

    @NotBlank
    private String language;

    public Long getProblemId() { return problemId; }
    public void setProblemId(Long problemId) { this.problemId = problemId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}
