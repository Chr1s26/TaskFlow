package com.project.taskFlow.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendOtpEmail(String to, String otp) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("Task Flow System - OTP");
        helper.setFrom("htetkyawswarlinn44@gmail.com");

        Context context = new Context();
        context.setVariable("otp", otp);
        context.setVariable("websiteUrl", "www.testFlow.org");

        String htmlContent = templateEngine.process("otp-mail-template", context);
        helper.setText(htmlContent, true);
        mailSender.send(mimeMessage);
    }
}
