package com.project.taskFlow.controller;

import com.project.taskFlow.model.User;
import com.project.taskFlow.model.constant.Role;
import com.project.taskFlow.repository.UserRepository;
import com.project.taskFlow.security.jwt.JwtUtils;
import com.project.taskFlow.security.request.LoginRequest;
import com.project.taskFlow.security.request.SignupRequest;
import com.project.taskFlow.security.response.MessageResponse;
import com.project.taskFlow.security.response.UserInfoResponse;
import com.project.taskFlow.security.services.UserDetailsImpl;
import com.project.taskFlow.service.authentication.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        }catch (AuthenticationException e){
            Map<String,Object> map = new HashMap<>();
            map.put("message","Bad credentials");
            map.put("status",false);

            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }

        UserInfoResponse userInfoResponse = authService.authenticateUser(authentication);
        return ResponseEntity.ok(userInfoResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
        if(userRepository.existsByName(signupRequest.getUsername())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if(userRepository.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already taken!"));
        }

        authService.registerUser(signupRequest);
        return ResponseEntity.ok(new MessageResponse("User register successfully!"));
    }

    @GetMapping("/username")
    public String getCurrentUsername(Authentication authentication){
        if(authentication != null){
            return authentication.getName();
        }else {
            return "Username is null";
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){
        UserInfoResponse response = authService.getUserDetails(authentication);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(){
        return ResponseEntity.ok().body(new MessageResponse("Logout successfully!"));
    }
}
