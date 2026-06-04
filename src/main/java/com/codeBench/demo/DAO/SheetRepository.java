package com.codeBench.demo.DAO;

import com.codeBench.demo.Entity.Sheet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SheetRepository extends JpaRepository<Sheet, Long> {

    Optional<Sheet> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<Sheet> findByPublishedTrue(Pageable pageable);

    @Query("SELECT s FROM Sheet s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND s.published = true")
    Page<Sheet> searchByTitle(@Param("keyword") String keyword, Pageable pageable);
}
