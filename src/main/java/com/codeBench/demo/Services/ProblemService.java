package com.codeBench.demo.Services;

import com.codeBench.demo.DTO.ProblemListResponse;
import com.codeBench.demo.DTO.ProblemRequest;
import com.codeBench.demo.DTO.ProblemResponse;
import com.codeBench.demo.Entity.Difficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProblemService {

    // Admin
    ProblemResponse createProblem(ProblemRequest request);
    List<ProblemResponse> createMultipleProblems(List<ProblemRequest> requests);
    ProblemResponse updateProblem(Long id, ProblemRequest request);
    void deleteProblem(Long id);
    void publishProblem(Long id);
    void unpublishProblem(Long id);

    // User / Public
    Page<ProblemListResponse> getAllPublishedProblems(Pageable pageable, String username);
    ProblemResponse getProblemBySlug(String slug);
    Page<ProblemResponse> getProblemsByTopicSlug(String topicSlug, Pageable pageable);
    Page<ProblemResponse> searchProblems(String keyword, Pageable pageable);
    Page<ProblemResponse> getProblemsByDifficulty(Difficulty difficulty, Pageable pageable);
}
