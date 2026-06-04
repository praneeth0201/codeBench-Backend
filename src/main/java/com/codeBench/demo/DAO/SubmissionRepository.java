package com.codeBench.demo.DAO;

import com.codeBench.demo.Entity.Submission;
import com.codeBench.demo.Entity.Verdict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByUsernameOrderByCreatedAtDesc(String username);
    List<Submission> findByUsernameAndProblemIdOrderByCreatedAtDesc(
            String username, Long problemId
    );

    Submission findFirstByUsernameAndProblemIdOrderByCreatedAtDesc(String username, Long problemId);

    List<Submission> findByUsernameAndVerdictOrderByCreatedAtDesc(
            String username,
            Verdict verdict
    );

    boolean existsByUsernameAndProblemIdAndVerdict(String username, Long problemId, Verdict verdict);
}


