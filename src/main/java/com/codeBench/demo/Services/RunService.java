package com.codeBench.demo.Services;

import com.codeBench.demo.Configuration.RabbitConfig;
import com.codeBench.demo.DAO.RunRecordRepository;
import com.codeBench.demo.DTO.RunRequest;
import com.codeBench.demo.Entity.RunRecord;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Mirrors SubmissionService — saves a RunRecord with PENDING status and
 * dispatches its ID to the run_queue after the transaction commits.
 */
@Service
public class RunService {

    private final RunRecordRepository runRecordRepository;
    private final RabbitTemplate rabbitTemplate;

    public RunService(RunRecordRepository runRecordRepository, RabbitTemplate rabbitTemplate) {
        this.runRecordRepository = runRecordRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public RunRecord createRun(String username, RunRequest request) {

        RunRecord run = new RunRecord();
        run.setUsername(username);
        run.setProblemId(request.getProblemId());
        run.setCode(request.getCode());
        run.setLanguage(request.getLanguage());
        run.setStatus("PENDING");

        run = runRecordRepository.save(run);
        runRecordRepository.flush();

        final Long runId = run.getId();

        // Publish only after the transaction commits so the worker can read the record
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        rabbitTemplate.convertAndSend(RabbitConfig.RUN_QUEUE_NAME, runId);
                    }
                }
        );

        return run;
    }
}
