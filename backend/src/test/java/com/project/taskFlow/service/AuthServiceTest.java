package com.project.taskFlow.service;

import com.project.taskFlow.exception.ResourceNotFoundException;
import com.project.taskFlow.model.User;
import com.project.taskFlow.repository.UserRepository;
import com.project.taskFlow.security.jwt.JwtUtils;
import com.project.taskFlow.security.request.SignupRequest;
import com.project.taskFlow.security.response.UserInfoResponse;
import com.project.taskFlow.security.services.UserDetailsImpl;
import com.project.taskFlow.service.authentication.AuthServiceImpl;
import com.project.taskFlow.service.otp.OtpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    JwtUtils jwtUtils;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    OtpService otpService;

    @InjectMocks
    AuthServiceImpl authService;

    @Test
    void shouldAuthenticateUserAndGenerateJwt() {
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L,
                "testUser",
                "test@test.com",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateTokenFromUsername(userDetails))
                .thenReturn("jwtToken");

        UserInfoResponse response = authService.authenticateUser(authentication);

        assertEquals("testUser", response.getUsername());
        assertEquals("jwtToken", response.getJwtToken());
        assertEquals("ROLE_USER", response.getRole());
    }

    @Test
    void shouldRegisterUser() {
        SignupRequest request = new SignupRequest();
        request.setUsername("testUser");
        request.setEmail("test@test.com");
        request.setPassword("password");
        request.setRole("USER");

        when(passwordEncoder.encode("password"))
                .thenReturn("encodedPassword");

        authService.registerUser(request);

        verify(userRepository).save(any(User.class));
    }

    @Test
    @WithMockUser
    void shouldReturnUserDetails() {

        Authentication authentication = mock(Authentication.class);

        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L,
                "testUser",
                "test@test.com",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(authentication.getPrincipal()).thenReturn(userDetails);

        UserInfoResponse response = authService.getUserDetails(authentication);

        assertEquals("testUser", response.getUsername());
        assertEquals("ROLE_USER", response.getRole());
    }

    @Test
    void shouldSendOtpForForgotPassword() {
        authService.forgotPassword("test@test.com");
        verify(otpService).sendOtp("test@test.com");
    }

    @Test
    void shouldVerifyOtp() {
        authService.verifyOtp("test@test.com","123456");
        verify(otpService).isOtpValid("test@test.com","123456");
    }

    @Test
    void shouldResetPassword() {
        User user = new User();
        user.setEmail("test@test.com");

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("newPassword"))
                .thenReturn("encodedPassword");

        authService.resetPassword("test@test.com","newPassword");

        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> authService.resetPassword("test@test.com","newPassword"));
    }

}

