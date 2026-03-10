package com.project.taskFlow.service.authentication;

import com.project.taskFlow.security.request.SignupRequest;
import com.project.taskFlow.security.response.UserInfoResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {
    UserInfoResponse authenticateUser(Authentication authentication);
    void registerUser(SignupRequest signupRequest);
    UserInfoResponse getUserDetails(Authentication authentication);
    void forgotPassword(String email);
    void verifyOtp(String email, String otp);
    void resetPassword(String email, String newPassword);
}
