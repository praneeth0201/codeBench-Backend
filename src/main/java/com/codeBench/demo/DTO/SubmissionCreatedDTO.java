package com.codeBench.demo.DTO;

public class SubmissionCreatedDTO {
    private Long submissionId;

    public SubmissionCreatedDTO() {}

    public SubmissionCreatedDTO(Long submissionId) {
        this.submissionId = submissionId;
    }

    public Long getSubmissionId() { return submissionId; }
    public void setSubmissionId(Long submissionId) { this.submissionId = submissionId; }
}
