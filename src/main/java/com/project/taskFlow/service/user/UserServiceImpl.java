package com.project.taskFlow.service.user;

import com.project.taskFlow.dto.UserCreateRequest;
import com.project.taskFlow.dto.UserResponse;
import com.project.taskFlow.dto.UserUpdateRequest;
import com.project.taskFlow.exception.ResourceNotFoundException;
import com.project.taskFlow.exception.UnauthorizedException;
import com.project.taskFlow.model.User;
import com.project.taskFlow.model.constant.Role;
import com.project.taskFlow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createUser(UserCreateRequest request) {
        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setEnable(true);

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User currentUser = getCurrentUser();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(!currentUser.getRole().equals(Role.ADMIN) &&
                !user.getId().equals(currentUser.getId())) {

            throw new UnauthorizedException("You cannot update this user");
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setProfileImageUrl(request.getProfileImageUrl());

        User updated = userRepository.save(user);
        return mapToResponse(updated);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::mapToResponse);
    }

    @Override
    public UserResponse getCurrentUserResponse() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole());
        userResponse.setProfileImageUrl(user.getProfileImageUrl());
        return userResponse;
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user;
    }

    private UserResponse mapToResponse(User user){
        UserResponse userResponse = new UserResponse();

        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole());
        userResponse.setProfileImageUrl(user.getProfileImageUrl());
        return userResponse;
    }
}
