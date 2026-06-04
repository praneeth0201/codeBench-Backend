package com.codeBench.demo.Controller;

import com.codeBench.demo.DTO.ProblemListResponse;
import com.codeBench.demo.DTO.ProblemResponse;
import com.codeBench.demo.Entity.Difficulty;
import com.codeBench.demo.Services.ProblemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {

    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @GetMapping
    public ResponseEntity<Page<ProblemListResponse>> getAllPublishedProblems(Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
                ? auth.getName() : null;
        return ResponseEntity.ok(problemService.getAllPublishedProblems(pageable, username));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ProblemResponse> getProblemBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(problemService.getProblemBySlug(slug));
    }

    @GetMapping("/topic/{topicSlug}")
    public ResponseEntity<Page<ProblemResponse>> getProblemsByTopicSlug(@PathVariable String topicSlug, Pageable pageable) {
        return ResponseEntity.ok(problemService.getProblemsByTopicSlug(topicSlug, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProblemResponse>> searchProblems(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(problemService.searchProblems(keyword, pageable));
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<Page<ProblemResponse>> getProblemsByDifficulty(@PathVariable Difficulty difficulty, Pageable pageable) {
        return ResponseEntity.ok(problemService.getProblemsByDifficulty(difficulty, pageable));
    }
}
