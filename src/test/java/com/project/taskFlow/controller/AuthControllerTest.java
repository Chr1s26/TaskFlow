package com.project.taskFlow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.taskFlow.repository.UserRepository;
import com.project.taskFlow.security.jwt.JwtUtils;
import com.project.taskFlow.security.request.*;
import com.project.taskFlow.security.response.UserInfoResponse;
import com.project.taskFlow.security.services.UserDetailsImpl;
import com.project.taskFlow.security.services.UserDetailsServiceImpl;
import com.project.taskFlow.service.authentication.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldLoginSuccessfully() throws Exception{
        LoginRequest request = new LoginRequest();
        request.setUsername("chris");
        request.setPassword("123");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        UserInfoResponse response = new UserInfoResponse();
        response.setId(1L);
        response.setUsername("testUser");
        response.setJwtToken("jwtToken123");
        response.setRole("USER");

        when(authService.authenticateUser(authentication))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/signin")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

    }

    @Test
    void shouldReturnUnauthorizedWhenLoginFails() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("wrongUser");
        request.setPassword("wrongPassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/signin")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRegisterUser() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setUsername("newUser");
        request.setEmail("new@test.com");
        request.setPassword("password123");
        request.setRole("USER");

        when(userRepository.existsByName("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);

        doNothing().when(authService).registerUser(request);

        mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnBadRequestWhenUsernameExists() throws Exception {

        SignupRequest request = new SignupRequest();
        request.setUsername("existingUser");
        request.setEmail("test@test.com");

        when(userRepository.existsByName("existingUser"))
                .thenReturn(true);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenEmailExists() throws Exception {

        SignupRequest request = new SignupRequest();
        request.setUsername("existingUser");
        request.setEmail("test@test.com");

        when(userRepository.existsByEmail("test@test.com"))
                .thenReturn(true);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnCurrentUsername() throws Exception {

        mockMvc.perform(get("/api/auth/username")
                        .principal(() -> "testUser"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUserDetails() throws Exception {

        Authentication authentication = mock(Authentication.class);

        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L,
                "testUser",
                "test@test.com",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(authentication.getPrincipal()).thenReturn(userDetails);

        UserInfoResponse response =
                new UserInfoResponse(1L, "testUser", "ROLE_USER");

        when(authService.getUserDetails(authentication))
                .thenReturn(response);

        mockMvc.perform(get("/api/auth/user")
                        .principal(authentication))
                .andExpect(status().isOk());
    }

    @Test
    void shouldLogoutSuccessfully() throws Exception {
        mockMvc.perform(post("/api/auth/signout"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldSendOtpForForgotPassword() throws Exception{
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@example.com");

        doNothing().when(authService).forgotPassword("test@example.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldVerifyOtp() throws Exception{
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail("test@example.com");
        request.setOtp("123456");

        doNothing().when(authService).verifyOtp("test@example.com","123456");

        mockMvc.perform(post("/api/auth/verify-otp")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldResetPassword() throws Exception{
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("test@example.com");
        request.setNewPassword("newPassword123");

        doNothing().when(authService).resetPassword("test@example.com","newPassword123");

        mockMvc.perform(post("/api/auth/reset-password")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}

//security test, and integration test
