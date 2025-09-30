package org.anyatasks.notifier.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailSendingService {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

    public void send(String emailAddress, String subject, String notificationMessage) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setSubject(subject);
            helper.setFrom(fromEmail);
            helper.setTo(emailAddress);
            helper.setText(notificationMessage);

            javaMailSender.send(message);
            log.info("EMAILSEND: email {} successfully sent to {}", notificationMessage, emailAddress);
        } catch (MessagingException e) {
            log.error("EMAILSEND: failed to send email to {}: {}", emailAddress, e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }

}

