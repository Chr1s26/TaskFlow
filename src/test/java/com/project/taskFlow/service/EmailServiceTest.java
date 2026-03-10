package com.project.taskFlow.service;
import com.project.taskFlow.service.email.EmailServiceImpl;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private TemplateEngine templateEngine;
    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void shouldSendOtpEmailSuccessfully() throws Exception {

        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("otp-mail-template"), any()))
                .thenReturn("<html>OTP Email</html>");

        emailService.sendOtpEmail("test@test.com", "123456");

        verify(mailSender).send(mimeMessage);
    }
}
