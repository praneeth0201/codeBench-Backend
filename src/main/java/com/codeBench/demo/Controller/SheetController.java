package com.codeBench.demo.Controller;

import com.codeBench.demo.DTO.SheetProblemResponse;
import com.codeBench.demo.DTO.SheetResponse;
import com.codeBench.demo.Services.SheetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sheets")
public class SheetController {

    private final SheetService sheetService;

    public SheetController(SheetService sheetService) {
        this.sheetService = sheetService;
    }

    @GetMapping
    public ResponseEntity<Page<SheetResponse>> getAllPublishedSheets(Pageable pageable) {
        return ResponseEntity.ok(sheetService.getAllPublishedSheets(pageable));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<SheetResponse> getSheetBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(sheetService.getSheetBySlug(slug));
    }

    @GetMapping("/{sheetId}/problems")
    public ResponseEntity<List<SheetProblemResponse>> getProblemsForSheet(@PathVariable Long sheetId) {
        return ResponseEntity.ok(sheetService.getProblemsForSheet(sheetId));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<SheetResponse>> searchSheets(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(sheetService.searchSheets(keyword, pageable));
    }
}
