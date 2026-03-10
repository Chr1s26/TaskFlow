package com.project.taskFlow.service;

import com.project.taskFlow.dto.UserCreateRequest;
import com.project.taskFlow.dto.UserResponse;
import com.project.taskFlow.dto.UserUpdateRequest;
import com.project.taskFlow.exception.ResourceNotFoundException;
import com.project.taskFlow.exception.UnauthorizedException;
import com.project.taskFlow.model.User;
import com.project.taskFlow.model.constant.Role;
import com.project.taskFlow.repository.UserRepository;
import com.project.taskFlow.service.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldCreateUser() {

        UserCreateRequest request = new UserCreateRequest();
        request.setName("testUser");
        request.setEmail("test@test.com");
        request.setPassword("password");
        request.setRole(Role.USER);

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("testUser");
        savedUser.setEmail("test@test.com");
        savedUser.setRole(Role.USER);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.createUser(request);

        assertEquals("testUser", response.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldUpdateUserSuccessfully() {

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(Role.USER);

        User existingUser = new User();
        existingUser.setId(1L);

        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("updated");
        request.setEmail("updated@test.com");

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByName("testUser")).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        UserResponse response = userService.updateUser(1L, request);

        assertEquals("updated", response.getName());
    }

    @Test
    void shouldThrowUnauthorizedWhenUpdatingOtherUser() {

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(Role.USER);

        User otherUser = new User();
        otherUser.setId(2L);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByName("testUser")).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));

        UserUpdateRequest request = new UserUpdateRequest();

        assertThrows(UnauthorizedException.class,
                () -> userService.updateUser(2L, request));
    }

    @Test
    void shouldDeleteUser() {

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingUser() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.deleteUser(1L));
    }

    @Test
    void shouldReturnUsers() {

        User user = new User();
        user.setId(1L);

        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));

        var result = userService.getUsers(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldReturnCurrentUser() {

        User user = new User();
        user.setName("testUser");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByName("testUser")).thenReturn(Optional.of(user));

        User result = userService.getCurrentUser();

        assertEquals("testUser", result.getName());
    }

    @Test
    void shouldReturnCurrentUserResponse() {

        User user = new User();
        user.setId(1L);
        user.setName("testUser");
        user.setEmail("test@test.com");
        user.setRole(Role.USER);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByName("testUser")).thenReturn(Optional.of(user));

        UserResponse response = userService.getCurrentUserResponse();

        assertEquals("testUser", response.getName());
        assertEquals("test@test.com", response.getEmail());
    }
}

