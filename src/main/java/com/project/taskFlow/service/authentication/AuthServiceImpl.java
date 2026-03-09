package com.project.taskFlow.service.authentication;

import com.project.taskFlow.dto.UserResponse;
import com.project.taskFlow.model.User;
import com.project.taskFlow.model.constant.Role;
import com.project.taskFlow.repository.UserRepository;
import com.project.taskFlow.security.jwt.JwtUtils;
import com.project.taskFlow.security.request.LoginRequest;
import com.project.taskFlow.security.request.SignupRequest;
import com.project.taskFlow.security.response.UserInfoResponse;
import com.project.taskFlow.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserInfoResponse authenticateUser(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);
        String role = userDetails.getAuthorities()
                .iterator()
                .next()
                .getAuthority();
        UserInfoResponse userInfoResponse = new UserInfoResponse(userDetails.getId(),jwtToken.toString(),userDetails.getUsername(),role);
        return userInfoResponse;
    }

    @Override
    public void registerUser(SignupRequest signupRequest) {
        User user = new User();
        user.setName(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());

        if(signupRequest.getRole() == null){
            user.setRole(Role.USER);
        }else{
            user.setRole(Role.valueOf(signupRequest.getRole()));
        }

        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setEnable(true);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public UserInfoResponse getUserDetails(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities()
                .iterator()
                .next()
                .getAuthority();
        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),userDetails.getUsername(),role);
        return response;
    }
}
