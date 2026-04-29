package com.codeBench.demo.DTO;


import lombok.Getter;

@Getter
public class SubmissionRequest {

    private Long problemId;
    private String code;
    private String language;
}
