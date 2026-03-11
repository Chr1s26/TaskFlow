package com.project.taskFlow.service;

import com.project.taskFlow.exception.BadRequestException;
import com.project.taskFlow.exception.ResourceNotFoundException;
import com.project.taskFlow.model.OtpToken;
import com.project.taskFlow.model.User;
import com.project.taskFlow.repository.OtpTokenRepository;
import com.project.taskFlow.repository.UserRepository;
import com.project.taskFlow.service.email.EmailServiceImpl;
import com.project.taskFlow.service.otp.OtpServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OtpServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpTokenRepository otpTokenRepository;

    @Mock
    private EmailServiceImpl emailService;

    @InjectMocks
    private OtpServiceImpl otpService;

    @Test
    void shouldGenerateSixDigitOtp() {
        String otp = otpService.generateOtpCode();
        assertNotNull(otp);
        assertEquals(6, otp.length());
    }

    @Test
    void shouldSendOtpSuccessfully() throws Exception {

        User user = new User();
        user.setEmail("test@test.com");

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        otpService.sendOtp("test@test.com");

        verify(otpTokenRepository).deleteByEmail("test@test.com");
        verify(otpTokenRepository).save(any(OtpToken.class));
        verify(emailService).sendOtpEmail(eq("test@test.com"), any());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> otpService.sendOtp("test@test.com"));
    }

    @Test
    void shouldValidateOtpSuccessfully() {

        OtpToken token = new OtpToken();
        token.setEmail("test@test.com");
        token.setOtpCode("123456");
        token.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        when(otpTokenRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(token));

        otpService.isOtpValid("test@test.com", "123456");

        verify(otpTokenRepository).delete(token);
    }

    @Test
    void shouldThrowExceptionWhenOtpInvalid() {

        OtpToken token = new OtpToken();
        token.setOtpCode("123456");
        token.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        when(otpTokenRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(token));

        assertThrows(BadRequestException.class,
                () -> otpService.isOtpValid("test@test.com", "000000"));
    }

    @Test
    void shouldThrowExceptionWhenOtpExpired() {

        OtpToken token = new OtpToken();
        token.setOtpCode("123456");
        token.setExpiryTime(LocalDateTime.now().minusMinutes(1));

        when(otpTokenRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(token));

        assertThrows(BadRequestException.class,
                () -> otpService.isOtpValid("test@test.com", "123456"));
    }
}

