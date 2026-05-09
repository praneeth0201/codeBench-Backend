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

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final RabbitTemplate rabbitTemplate;

    public SubmissionService(SubmissionRepository submissionRepository, RabbitTemplate rabbitTemplate) {
        this.submissionRepository = submissionRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public Submission createSubmission(String username, SubmissionRequest request) {

        Submission submission = new Submission();
        submission.setUsername(username);
        submission.setProblemId(request.getProblemId());
        submission.setCode(request.getCode());
        submission.setLanguage(request.getLanguage());
        submission.setStatus("PENDING");

        submission = submissionRepository.save(submission);

        System.out.println("=== SAVED ID: " + submission.getId());
        submissionRepository.flush();
        System.out.println("=== FLUSH DONE");
        boolean exists = submissionRepository.findById(submission.getId()).isPresent();
        System.out.println("=== EXISTS IN DB: " + exists);

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

        return submission;
    }

    public List<Submission> getUserHistory(String username) {
        return submissionRepository.findByUsernameAndStatusOrderByCreatedAtDesc(
                username,
                "ACCEPTED"
        );
    }

    public List<Submission> getProblemHistory(String username, Long problemId) {
        return submissionRepository
                .findByUsernameAndProblemIdOrderByCreatedAtDesc(username, problemId);
    }


}



