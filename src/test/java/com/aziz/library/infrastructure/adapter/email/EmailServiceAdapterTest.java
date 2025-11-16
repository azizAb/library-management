package com.aziz.library.infrastructure.adapter.email;

import static org.mockito.Mockito.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import java.lang.reflect.Field;


class EmailServiceAdapterTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceAdapter emailServiceAdapter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Set private fields via reflection
        setField(emailServiceAdapter, "fromEmail", "noreply@example.com");
        setField(emailServiceAdapter, "appName", "TestApp");
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = EmailServiceAdapter.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void sendOtpEmail_shouldSendEmail() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailServiceAdapter.sendOtpEmail("user@example.com", "John Doe", "123456");

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendWelcomeEmail_shouldSendEmail() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailServiceAdapter.sendWelcomeEmail("user@example.com", "Jane Doe");

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendAccountLockedEmail_shouldSendEmail() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailServiceAdapter.sendAccountLockedEmail("user@example.com", "Jane Doe", 15);

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendEmail_shouldHandleMessagingException() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException(new MessagingException("fail"))).when(mailSender).send(mimeMessage);

        // Use reflection to call private sendEmail method
        String to = "fail@example.com";
        String subject = "subject";
        String htmlContent = "<html></html>";
        var method = EmailServiceAdapter.class.getDeclaredMethod("sendEmail", String.class, String.class, String.class);
        method.setAccessible(true);
        method.invoke(emailServiceAdapter, to, subject, htmlContent);

        verify(mailSender).send(mimeMessage);
    }
}