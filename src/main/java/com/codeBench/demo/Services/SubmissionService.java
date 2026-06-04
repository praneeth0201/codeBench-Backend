package com.codeBench.demo.Services;

import com.codeBench.demo.DAO.SubmissionRepository;
import com.codeBench.demo.DTO.SubmissionRequest;
import com.codeBench.demo.Entity.Submission;
import com.codeBench.demo.Configuration.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.stream.Collectors;
import com.codeBench.demo.Entity.SubmissionStatus;
import com.codeBench.demo.Entity.Verdict;
import com.codeBench.demo.DTO.SubmissionCreatedDTO;
import com.codeBench.demo.DTO.SubmissionResultDTO;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final RabbitTemplate rabbitTemplate;

    public SubmissionService(SubmissionRepository submissionRepository, RabbitTemplate rabbitTemplate) {
        this.submissionRepository = submissionRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public SubmissionCreatedDTO createSubmission(String username, SubmissionRequest request) {

        Submission submission = new Submission();
        submission.setUsername(username);
        submission.setProblemId(request.getProblemId());
        submission.setCode(request.getCode());
        submission.setLanguage(request.getLanguage());
        submission.setStatus(SubmissionStatus.PENDING);

        submission = submissionRepository.save(submission);


        submissionRepository.flush();

        boolean exists = submissionRepository.findById(submission.getId()).isPresent();


        final Long submissionId = submission.getId();


        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        rabbitTemplate.convertAndSend(
                                RabbitConfig.QUEUE_NAME,
                                submissionId
                        );
                    }
                }
        );

        return new SubmissionCreatedDTO(submission.getId());
    }

    public List<SubmissionResultDTO> getUserHistory(String username) {
        return submissionRepository.findByUsernameAndVerdictOrderByCreatedAtDesc(
                username,
                Verdict.ACCEPTED
        ).stream().map(this::toResultDTO).collect(Collectors.toList());
    }

    public List<SubmissionResultDTO> getProblemHistory(String username, Long problemId) {
        return submissionRepository
                .findByUsernameAndProblemIdOrderByCreatedAtDesc(username, problemId)
                .stream().map(this::toResultDTO).collect(Collectors.toList());
    }

    public String getLatestProblemSubmission(String username, Long problemId) {
        Submission submission = submissionRepository.findFirstByUsernameAndProblemIdOrderByCreatedAtDesc(username, problemId);
        if (submission == null) return null;
        return submission.getCode();
    }

    public SubmissionResultDTO toResultDTO(Submission s) {
        return new SubmissionResultDTO(
            s.getId(),
            s.getStatus(),
            s.getVerdict(),
            s.getExecutionTime(),
            s.getPassedTestCases(),
            s.getTotalTestCases(),
            s.getFailedInput(),
            s.getExpectedOutput(),
            s.getActualOutput(),
            s.getCompileError()
        );
    }


}



