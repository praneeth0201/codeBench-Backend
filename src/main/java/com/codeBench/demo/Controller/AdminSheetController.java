package com.codeBench.demo.Controller;

import com.codeBench.demo.DTO.SheetRequest;
import com.codeBench.demo.DTO.SheetResponse;
import com.codeBench.demo.Services.SheetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/sheets")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSheetController {

    private final SheetService sheetService;

    public AdminSheetController(SheetService sheetService) {
        this.sheetService = sheetService;
    }

    @PostMapping
    public ResponseEntity<SheetResponse> createSheet(@Valid @RequestBody SheetRequest request) {
        return new ResponseEntity<>(sheetService.createSheet(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SheetResponse> updateSheet(@PathVariable Long id, @Valid @RequestBody SheetRequest request) {
        return ResponseEntity.ok(sheetService.updateSheet(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSheet(@PathVariable Long id) {
        sheetService.deleteSheet(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{sheetId}/problems/{problemId}")
    public ResponseEntity<Void> addProblemToSheet(@PathVariable Long sheetId, @PathVariable Long problemId) {
        sheetService.addProblemToSheet(sheetId, problemId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{sheetId}/problems/{problemId}")
    public ResponseEntity<Void> removeProblemFromSheet(@PathVariable Long sheetId, @PathVariable Long problemId) {
        sheetService.removeProblemFromSheet(sheetId, problemId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<Void> publishSheet(@PathVariable Long id) {
        sheetService.publishSheet(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/unpublish")
    public ResponseEntity<Void> unpublishSheet(@PathVariable Long id) {
        sheetService.unpublishSheet(id);
        return ResponseEntity.ok().build();
    }
}
