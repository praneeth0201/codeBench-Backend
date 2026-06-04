package com.codeBench.demo.DAO;

import com.codeBench.demo.Entity.RunRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RunRecordRepository extends JpaRepository<RunRecord, Long> {

    List<RunRecord> findByUsernameOrderByCreatedAtDesc(String username);
}
