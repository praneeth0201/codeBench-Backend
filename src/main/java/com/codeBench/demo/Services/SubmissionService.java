package com.codeBench.demo.Services;

import com.codeBench.demo.DAO.SubmissionRepository;
import com.codeBench.demo.DTO.SubmissionRequest;
import com.codeBench.demo.Entity.Submission;
import org.springframework.stereotype.Service;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;

    public SubmissionService(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    public Submission createSubmission(String username, SubmissionRequest request) {

        Submission submission = new Submission();
        submission.setUsername(username);
        submission.setProblemId(request.getProblemId());
        submission.setCode(request.getCode());
        submission.setLanguage(request.getLanguage());
        submission.setStatus("PENDING");

        submission =submissionRepository.save(submission);

        processSubmission(submission);

        return submission;
    }

    public void processSubmission(Submission submission) {

        try {
            Thread.sleep(2000);

            submission.setStatus("RUNNING");
            submissionRepository.save(submission);

            Thread.sleep(2000);


            if (submission.getCode().contains("print")) {
                submission.setStatus("SUCCESS");
                submission.setOutput("Hello World");
            } else {
                submission.setStatus("FAILED");
                submission.setOutput("Wrong Answer");
            }

            submission.setExecutionTime(2000L);

        } catch (Exception e) {
            submission.setStatus("FAILED");
            submission.setOutput("Error during execution");
        }

        submissionRepository.save(submission);
    }
}
