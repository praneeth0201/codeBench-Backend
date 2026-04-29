package com.codeBench.demo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "submissions")
@ToString
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private Long problemId;

    @Column(columnDefinition = "TEXT")
    private String code;

    private String language;

    private String status; // PENDING, RUNNING, SUCCESS, FAILED

    @Column(columnDefinition = "TEXT")
    private String output;

    private Long executionTime;
}
