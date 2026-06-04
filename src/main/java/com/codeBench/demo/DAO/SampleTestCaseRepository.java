package com.codeBench.demo.DAO;

import com.codeBench.demo.Entity.SampleTestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SampleTestCaseRepository extends JpaRepository<SampleTestCase, Long> {

    /** Fetch all samples for a given problem, ordered by their display index */
    List<SampleTestCase> findByProblemIdOrderByOrderIndexAsc(Long problemId);
}
