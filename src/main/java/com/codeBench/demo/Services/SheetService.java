package com.codeBench.demo.Services;

import com.codeBench.demo.DTO.SheetProblemResponse;
import com.codeBench.demo.DTO.SheetRequest;
import com.codeBench.demo.DTO.SheetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SheetService {

    // Admin
    SheetResponse createSheet(SheetRequest request);
    SheetResponse updateSheet(Long id, SheetRequest request);
    void deleteSheet(Long id);
    void publishSheet(Long id);
    void unpublishSheet(Long id);

    void addProblemToSheet(Long sheetId, Long problemId);
    void removeProblemFromSheet(Long sheetId, Long problemId);

    // User / Public
    Page<SheetResponse> getAllPublishedSheets(Pageable pageable);
    SheetResponse getSheetBySlug(String slug);
    List<SheetProblemResponse> getProblemsForSheet(Long sheetId);
    Page<SheetResponse> searchSheets(String keyword, Pageable pageable);
}
