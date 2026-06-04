package com.codeBench.demo.DAO;

import com.codeBench.demo.Entity.Difficulty;
import com.codeBench.demo.Entity.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

    Optional<Problem> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<Problem> findByPublishedTrue(Pageable pageable);

    Page<Problem> findByDifficultyAndPublishedTrue(Difficulty difficulty, Pageable pageable);

    /** Case-insensitive title search with pagination */
    @Query("SELECT p FROM Problem p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.published = true")
    Page<Problem> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

    /** All published problems for a given topic */
    @Query("""
           SELECT p FROM Problem p
           JOIN p.problemTopics pt
           WHERE pt.topic.id = :topicId
           AND p.published = true
           """)
    Page<Problem> findByTopicId(@Param("topicId") Long topicId, Pageable pageable);
}
