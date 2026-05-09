package com.codeBench.demo.Services;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String token) throws MessagingException {

        String verificationLink =
                "http://localhost:8080/api/auth/verify?token=" + token;
        MimeMessage message =
                mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true);

        helper.setFrom(
                "CodeBench <yourgmail@gmail.com>"
        );

        helper.setTo(to);

        helper.setSubject(
                "CodeBench Email Verification"
        );

        String htmlContent = """
                <div style="
                    font-family: Arial;
                    padding: 20px;
                ">

                    <h2>
                        Welcome to CodeBench
                    </h2>

                    <p>
                        Thank you for registering.
                    </p>

                    <p>
                        Click the button below
                        to verify your account:
                    </p>

                    <a href="%s"
                       style="
                           display:inline-block;
                           padding:12px 20px;
                           background:#2563eb;
                           color:white;
                           text-decoration:none;
                           border-radius:6px;
                           font-weight:bold;
                       ">
                        Verify Account
                    </a>

                    <p style="
                        margin-top:20px;
                        color:gray;
                        font-size:14px;
                    ">
                        If you did not create
                        this account,
                        you can safely ignore
                        this email.
                    </p>

                </div>
                """.formatted(verificationLink);

        helper.setText(htmlContent, true);

        mailSender.send(message);

        System.out.println(
                "HTML verification email sent"
        );
    }
}