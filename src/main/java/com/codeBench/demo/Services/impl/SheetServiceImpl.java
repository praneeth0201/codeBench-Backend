package com.codeBench.demo.Services.impl;

import com.codeBench.demo.DAO.ProblemRepository;
import com.codeBench.demo.DAO.SheetProblemRepository;
import com.codeBench.demo.DAO.SheetRepository;
import com.codeBench.demo.DTO.SheetProblemResponse;
import com.codeBench.demo.DTO.SheetRequest;
import com.codeBench.demo.DTO.SheetResponse;
import com.codeBench.demo.Entity.Problem;
import com.codeBench.demo.Entity.Sheet;
import com.codeBench.demo.Entity.SheetProblem;
import com.codeBench.demo.Services.SheetService;
import com.codeBench.demo.exception.DuplicateResourceException;
import com.codeBench.demo.exception.ResourceNotFoundException;
import com.codeBench.demo.util.SlugUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SheetServiceImpl implements SheetService {

    private final SheetRepository sheetRepository;
    private final ProblemRepository problemRepository;
    private final SheetProblemRepository sheetProblemRepository;

    public SheetServiceImpl(SheetRepository sheetRepository, ProblemRepository problemRepository, SheetProblemRepository sheetProblemRepository) {
        this.sheetRepository = sheetRepository;
        this.problemRepository = problemRepository;
        this.sheetProblemRepository = sheetProblemRepository;
    }

    @Override
    @Transactional
    public SheetResponse createSheet(SheetRequest request) {
        String slug = SlugUtil.generateSlug(request.getTitle());
        if (sheetRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Sheet with this title already exists.");
        }

        Sheet sheet = new Sheet();
        sheet.setTitle(request.getTitle());
        sheet.setSlug(slug);
        sheet.setDescription(request.getDescription());
        sheet.setPublished(request.getPublished() != null ? request.getPublished() : false);

        return toResponse(sheetRepository.save(sheet));
    }

    @Override
    @Transactional
    public SheetResponse updateSheet(Long id, SheetRequest request) {
        Sheet sheet = getSheetEntity(id);

        String newSlug = SlugUtil.generateSlug(request.getTitle());
        if (!sheet.getSlug().equals(newSlug) && sheetRepository.existsBySlug(newSlug)) {
            throw new DuplicateResourceException("Another sheet with this title already exists.");
        }

        sheet.setTitle(request.getTitle());
        sheet.setSlug(newSlug);
        sheet.setDescription(request.getDescription());
        if (request.getPublished() != null) {
            sheet.setPublished(request.getPublished());
        }

        return toResponse(sheetRepository.save(sheet));
    }

    @Override
    @Transactional
    public void deleteSheet(Long id) {
        Sheet sheet = getSheetEntity(id);
        sheetRepository.delete(sheet);
    }

    @Override
    @Transactional
    public void publishSheet(Long id) {
        Sheet sheet = getSheetEntity(id);
        sheet.setPublished(true);
        sheetRepository.save(sheet);
    }

    @Override
    @Transactional
    public void unpublishSheet(Long id) {
        Sheet sheet = getSheetEntity(id);
        sheet.setPublished(false);
        sheetRepository.save(sheet);
    }

    @Override
    @Transactional
    public void addProblemToSheet(Long sheetId, Long problemId) {
        Sheet sheet = getSheetEntity(sheetId);
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));

        if (sheetProblemRepository.existsBySheetIdAndProblemId(sheetId, problemId)) {
            throw new DuplicateResourceException("Problem is already in this sheet.");
        }

        Integer maxOrderIndex = sheetProblemRepository.findMaxOrderIndexBySheetId(sheetId);
        int nextOrderIndex = (maxOrderIndex == null) ? 1 : maxOrderIndex + 1;

        SheetProblem sheetProblem = new SheetProblem();
        sheetProblem.setSheet(sheet);
        sheetProblem.setProblem(problem);
        sheetProblem.setOrderIndex(nextOrderIndex);

        sheetProblemRepository.save(sheetProblem);
    }

    @Override
    @Transactional
    public void removeProblemFromSheet(Long sheetId, Long problemId) {
        SheetProblem sheetProblem = sheetProblemRepository.findBySheetIdAndProblemId(sheetId, problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem is not in this sheet"));
        sheetProblemRepository.delete(sheetProblem);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SheetResponse> getAllPublishedSheets(Pageable pageable) {
        return sheetRepository.findByPublishedTrue(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public SheetResponse getSheetBySlug(String slug) {
        return sheetRepository.findBySlug(slug)
                .filter(Sheet::getPublished)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Sheet not found or not published"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SheetProblemResponse> getProblemsForSheet(Long sheetId) {
        getSheetEntity(sheetId); // Validate sheet exists
        return sheetProblemRepository.findBySheetIdOrderByOrderIndexAsc(sheetId)
                .stream()
                .map(sp -> SheetProblemResponse.builder()
                        .id(sp.getId())
                        .sheetId(sp.getSheet().getId())
                        .problemId(sp.getProblem().getId())
                        .orderIndex(sp.getOrderIndex())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SheetResponse> searchSheets(String keyword, Pageable pageable) {
        return sheetRepository.searchByTitle(keyword, pageable).map(this::toResponse);
    }

    private Sheet getSheetEntity(Long id) {
        return sheetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sheet not found"));
    }

    private SheetResponse toResponse(Sheet sheet) {
        return SheetResponse.builder()
                .id(sheet.getId())
                .title(sheet.getTitle())
                .slug(sheet.getSlug())
                .description(sheet.getDescription())
                .published(sheet.getPublished())
                .createdAt(sheet.getCreatedAt())
                .updatedAt(sheet.getUpdatedAt())
                .build();
    }
}
