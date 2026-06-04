package com.codeBench.demo.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SheetProblemRequest {

    @NotNull
    private Long problemId;

    /** Optional explicit order; if null, appended at end */
    @Positive
    private Integer orderIndex;
}
