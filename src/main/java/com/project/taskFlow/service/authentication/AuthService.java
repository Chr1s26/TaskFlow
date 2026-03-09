package com.project.taskFlow.service.authentication;

import com.project.taskFlow.security.request.SignupRequest;
import com.project.taskFlow.security.response.UserInfoResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {
    public UserInfoResponse authenticateUser(Authentication authentication);
    public void registerUser(SignupRequest signupRequest);
    public UserInfoResponse getUserDetails(Authentication authentication);
}
