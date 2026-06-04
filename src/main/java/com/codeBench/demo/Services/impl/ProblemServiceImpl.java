package com.codeBench.demo.Services.impl;

import com.codeBench.demo.DAO.ProblemRepository;
import com.codeBench.demo.DAO.SubmissionRepository;
import com.codeBench.demo.DAO.TopicRepository;
import com.codeBench.demo.DTO.ProblemListResponse;
import com.codeBench.demo.DTO.ProblemRequest;
import com.codeBench.demo.DTO.ProblemResponse;
import com.codeBench.demo.DTO.SampleTestCaseResponse;
import com.codeBench.demo.Entity.Difficulty;
import com.codeBench.demo.Entity.Problem;
import com.codeBench.demo.Entity.Topic;
import com.codeBench.demo.Entity.Verdict;
import com.codeBench.demo.Services.ProblemService;
import com.codeBench.demo.Services.TestCaseService;
import com.codeBench.demo.exception.DuplicateResourceException;
import com.codeBench.demo.exception.ResourceNotFoundException;
import com.codeBench.demo.util.SlugUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeBench.demo.DAO.SampleTestCaseRepository;
import com.codeBench.demo.Entity.SampleTestCase;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final TopicRepository topicRepository;
    private final SampleTestCaseRepository sampleTestCaseRepository;
    private final TestCaseService testCaseService;
    private final SubmissionRepository submissionRepository;

    public ProblemServiceImpl(ProblemRepository problemRepository, TopicRepository topicRepository, SampleTestCaseRepository sampleTestCaseRepository, TestCaseService testCaseService, SubmissionRepository submissionRepository) {
        this.problemRepository = problemRepository;
        this.topicRepository = topicRepository;
        this.sampleTestCaseRepository = sampleTestCaseRepository;
        this.testCaseService = testCaseService;
        this.submissionRepository = submissionRepository;
    }

    @Override
    @Transactional
    public ProblemResponse createProblem(ProblemRequest request) {
        String slug = SlugUtil.generateSlug(request.getTitle());
        if (problemRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Problem with title already exists (slug conflict).");
        }

        Problem problem = Problem.builder()
                .title(request.getTitle())
                .slug(slug)
                .statement(request.getStatement())
                .inputFormat(request.getInputFormat())
                .outputFormat(request.getOutputFormat())
                .constraints(request.getConstraints())
                .difficulty(request.getDifficulty())
                .timeLimit(request.getTimeLimit())
                .memoryLimit(request.getMemoryLimit())
                .published(request.getPublished() != null ? request.getPublished() : false)
                .acceptanceRate(0.0)
                .totalSubmissions(0L)
                .totalAccepted(0L)
                .build();

        Problem savedProblem = problemRepository.save(problem);

        if (request.getSampleTestCases() != null && !request.getSampleTestCases().isEmpty()) {
            List<SampleTestCase> samples = request.getSampleTestCases().stream().map(req -> SampleTestCase.builder()
                    .problem(savedProblem)
                    .orderIndex(req.getOrderIndex())
                    .input(req.getInput())
                    .expectedOutput(req.getExpectedOutput())
                    .description(req.getDescription())
                    .build()).collect(Collectors.toList());
            sampleTestCaseRepository.saveAll(samples);
        }

        if (request.getTestCases() != null) {
            testCaseService.createMultipleTestCases(savedProblem.getId(), request.getTestCases());
        }

        return toResponse(savedProblem);
    }

    @Override
    @Transactional
    public List<ProblemResponse> createMultipleProblems(List<ProblemRequest> requests) {
        return requests.stream()
                .map(this::createProblem)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProblemResponse updateProblem(Long id, ProblemRequest request) {
        Problem problem = getProblemEntity(id);

        String newSlug = SlugUtil.generateSlug(request.getTitle());
        if (!problem.getSlug().equals(newSlug) && problemRepository.existsBySlug(newSlug)) {
            throw new DuplicateResourceException("Another problem with this title already exists.");
        }

        problem.setTitle(request.getTitle());
        problem.setSlug(newSlug);
        problem.setStatement(request.getStatement());
        problem.setInputFormat(request.getInputFormat());
        problem.setOutputFormat(request.getOutputFormat());
        problem.setConstraints(request.getConstraints());
        problem.setDifficulty(request.getDifficulty());
        problem.setTimeLimit(request.getTimeLimit());
        problem.setMemoryLimit(request.getMemoryLimit());
        
        if (request.getPublished() != null) {
            problem.setPublished(request.getPublished());
        }

        return toResponse(problemRepository.save(problem));
    }

    @Override
    @Transactional
    public void deleteProblem(Long id) {
        Problem problem = getProblemEntity(id);
        problemRepository.delete(problem);
    }

    @Override
    @Transactional
    public void publishProblem(Long id) {
        Problem problem = getProblemEntity(id);
        problem.setPublished(true);
        problemRepository.save(problem);
    }

    @Override
    @Transactional
    public void unpublishProblem(Long id) {
        Problem problem = getProblemEntity(id);
        problem.setPublished(false);
        problemRepository.save(problem);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProblemListResponse> getAllPublishedProblems(Pageable pageable, String username) {
        return problemRepository.findByPublishedTrue(pageable).map(p -> {
            boolean solved = username != null &&
                submissionRepository.existsByUsernameAndProblemIdAndVerdict(username, p.getId(), Verdict.ACCEPTED);
            return new ProblemListResponse(
                p.getId(),
                p.getTitle(),
                p.getSlug(),
                p.getDifficulty(),
                p.getAcceptanceRate(),
                solved
            );
        });
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemResponse getProblemBySlug(String slug) {
        return problemRepository.findBySlug(slug)
                .filter(Problem::getPublished)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found or not published"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProblemResponse> getProblemsByTopicSlug(String topicSlug, Pageable pageable) {
        Topic topic = topicRepository.findBySlug(topicSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
        return problemRepository.findByTopicId(topic.getId(), pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProblemResponse> searchProblems(String keyword, Pageable pageable) {
        return problemRepository.searchByTitle(keyword, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProblemResponse> getProblemsByDifficulty(Difficulty difficulty, Pageable pageable) {
        return problemRepository.findByDifficultyAndPublishedTrue(difficulty, pageable).map(this::toResponse);
    }

    private Problem getProblemEntity(Long id) {
        return problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found with id: " + id));
    }

    private ProblemResponse toResponse(Problem p) {
        return ProblemResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .slug(p.getSlug())
                .statement(p.getStatement())
                .inputFormat(p.getInputFormat())
                .outputFormat(p.getOutputFormat())
                .constraints(p.getConstraints())
                .difficulty(p.getDifficulty())
                .timeLimit(p.getTimeLimit())
                .memoryLimit(p.getMemoryLimit())
                .acceptanceRate(p.getAcceptanceRate())
                .sampleTestCases(p.getSampleTestCases() != null ? p.getSampleTestCases().stream()
                        .map(s -> SampleTestCaseResponse.builder()
                                .id(s.getId())
                                .problemId(p.getId())
                                .orderIndex(s.getOrderIndex())
                                .input(s.getInput())
                                .expectedOutput(s.getExpectedOutput())
                                .description(s.getDescription())
                                .build())
                        .collect(Collectors.toList()) : null)
                .build();
    }
}
