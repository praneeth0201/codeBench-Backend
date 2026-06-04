package com.codeBench.demo.DAO;

import com.codeBench.demo.Entity.ProblemTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemTopicRepository extends JpaRepository<ProblemTopic, Long> {

    boolean existsByProblemIdAndTopicId(Long problemId, Long topicId);

    List<ProblemTopic> findByProblemId(Long problemId);

    List<ProblemTopic> findByTopicId(Long topicId);

    @Modifying
    @Query("DELETE FROM ProblemTopic pt WHERE pt.problem.id = :problemId AND pt.topic.id = :topicId")
    void deleteByProblemIdAndTopicId(@Param("problemId") Long problemId, @Param("topicId") Long topicId);
}
