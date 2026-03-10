package com.project.taskFlow.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.taskFlow.model.OtpToken;
import com.project.taskFlow.repository.OtpTokenRepository;
import com.project.taskFlow.security.request.*;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.app.jwtSecret=ThisIsAVeryLongSecretKeyForJwtTestingPurpose123456789",
        "spring.app.jwtExpirationMs=86400000",
        "spring.app.jwtCookieName= cookieName"
})
@AutoConfigureMockMvc
public class AuthIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private OtpTokenRepository otpTokenRepository;

    @MockitoBean
    private JavaMailSender javaMailSender;

    @Test
    void shouldSignupUser() throws Exception {

        SignupRequest request = new SignupRequest();
        request.setUsername("integrationUser");
        request.setEmail("integration@test.com");
        request.setPassword("password123");
        request.setRole("USER");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldLoginUser() throws Exception {

        SignupRequest signup = new SignupRequest();
        signup.setUsername("loginUser");
        signup.setEmail("login@test.com");
        signup.setPassword("password123");
        signup.setRole("USER");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup)));

        LoginRequest login = new LoginRequest();
        login.setUsername("loginUser");
        login.setPassword("password123");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldSendOtp() throws Exception {

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        SignupRequest signup = new SignupRequest();
        signup.setUsername("otpUser");
        signup.setEmail("otp@test.com");
        signup.setPassword("password123");
        signup.setRole("USER");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup)));

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("otp@test.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldVerifyOtp() throws Exception {

        SignupRequest signup = new SignupRequest();
        signup.setUsername("verifyUser");
        signup.setEmail("verify@test.com");
        signup.setPassword("password123");
        signup.setRole("USER");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup)));

        ForgotPasswordRequest forgot = new ForgotPasswordRequest();
        forgot.setEmail("verify@test.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgot)));

        OtpToken token = otpTokenRepository.findByEmail("verify@test.com").get();

        VerifyOtpRequest verify = new VerifyOtpRequest();
        verify.setEmail("verify@test.com");
        verify.setOtp(token.getOtpCode());

        mockMvc.perform(post("/api/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verify)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldResetPassword() throws Exception {

        SignupRequest signup = new SignupRequest();
        signup.setUsername("verifyUser");
        signup.setEmail("verify@test.com");
        signup.setPassword("password123");
        signup.setRole("USER");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup)));

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("verify@test.com");
        request.setNewPassword("newPassword123");

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

}
