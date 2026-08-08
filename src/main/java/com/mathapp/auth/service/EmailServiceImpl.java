package com.mathapp.auth.service;

import org.springframework.beans.factory.annotation.Value;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordResetEmail(
            String to,
            String userName,
            String resetLink) {

        try {

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    true);

            helper.setFrom(fromEmail);

            helper.setTo(to);

            helper.setSubject(
                    "Reset Your MathApp Password");

            String html = """
                    <html>
                    <body>

                    <h2>Hello %s</h2>

                    <p>
                    We received a request to reset your password.
                    </p>

                    <p>
                    Click the button below:
                    </p>


                    <a href="%s"
                    style="
                    background:#2563eb;
                    color:white;
                    padding:12px 20px;
                    text-decoration:none;
                    border-radius:5px;
                    ">
                    Reset Password
                    </a>


                    <p>
                    This link expires in 30 minutes.
                    </p>


                    <p>
                    If you didn't request this, ignore this email.
                    </p>


                    </body>
                    </html>
                    """.formatted(
                    userName,
                    resetLink);

            helper.setText(
                    html,
                    true);

            mailSender.send(message);

        } catch (MessagingException e) {

            throw new RuntimeException(
                    "Unable to send email");

        }

    }
}