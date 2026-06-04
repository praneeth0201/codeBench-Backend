package com.codeBench.demo.DAO;

import com.codeBench.demo.Entity.SheetProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SheetProblemRepository extends JpaRepository<SheetProblem, Long> {

    boolean existsBySheetIdAndProblemId(Long sheetId, Long problemId);

    /** Returns all problems in a sheet ordered by their position */
    List<SheetProblem> findBySheetIdOrderByOrderIndexAsc(Long sheetId);

    @Modifying
    @Query("DELETE FROM SheetProblem sp WHERE sp.sheet.id = :sheetId AND sp.problem.id = :problemId")
    void deleteBySheetIdAndProblemId(@Param("sheetId") Long sheetId, @Param("problemId") Long problemId);

    @Query("SELECT COALESCE(MAX(sp.orderIndex), 0) FROM SheetProblem sp WHERE sp.sheet.id = :sheetId")
    Integer findMaxOrderIndexBySheetId(@Param("sheetId") Long sheetId);
    Optional<SheetProblem> findBySheetIdAndProblemId(Long sheetId, Long problemId);
}
