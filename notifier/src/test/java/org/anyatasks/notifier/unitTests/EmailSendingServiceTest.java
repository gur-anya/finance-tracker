package org.anyatasks.notifier.unitTests;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.anyatasks.notifier.services.EmailSendingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailSendingServiceTest {
    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailSendingService emailSendingService;
    @Captor
    private ArgumentCaptor<MimeMessage> messageCaptor;

    @Test
    void send_ShouldSendEmailSuccessfully() throws MessagingException, IOException {
        String to = "test@example.com";
        String subject = "subject";
        String body = "test message";
        ReflectionTestUtils.setField(emailSendingService, "fromEmail", "from@example.com");

        MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        emailSendingService.send(to, subject, body);

        verify(javaMailSender, times(1)).send(messageCaptor.capture());

        MimeMessage capturedMessage = messageCaptor.getValue();
        assertThat(subject).isEqualTo(capturedMessage.getSubject());
        assertThat(to).isEqualTo(capturedMessage.getRecipients(Message.RecipientType.TO)[0].toString());
        assertThat(capturedMessage.getContent().toString().contains(body)).isTrue();
        assertThat("from@example.com").isEqualTo(capturedMessage.getFrom()[0].toString());
    }

    @Test
    void send_ShouldThrowRuntimeException() {
        String to = "test@example.com";
        String subject = "subject";
        String body = "test message";
        ReflectionTestUtils.setField(emailSendingService, "fromEmail", "from@example.com");
        MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new MailSendException("Connection failed"))
            .when(javaMailSender).send(any(MimeMessage.class));

        assertThrows(RuntimeException.class, () -> emailSendingService.send(to, subject, body));

        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }
}