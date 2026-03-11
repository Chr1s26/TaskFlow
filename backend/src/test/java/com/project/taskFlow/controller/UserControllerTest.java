package com.project.taskFlow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.taskFlow.dto.UserCreateRequest;
import com.project.taskFlow.dto.UserResponse;
import com.project.taskFlow.dto.UserUpdateRequest;
import com.project.taskFlow.model.constant.Role;
import com.project.taskFlow.repository.UserRepository;
import com.project.taskFlow.security.jwt.JwtUtils;
import com.project.taskFlow.security.services.UserDetailsServiceImpl;
import com.project.taskFlow.service.authentication.AuthService;
import com.project.taskFlow.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    @MockitoBean
    private UserService userService;

    @Autowired
    MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldGetUsers() throws Exception {
        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setName("Chris");
        response.setEmail("chris123@gmail.com");
        response.setRole(Role.USER);
        response.setProfileImageUrl(null);

        when(userService.getUsers(null)).thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldCreateUser() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("Chris");
        request.setEmail("chris123@gmail.com");
        request.setPassword("123456");
        request.setRole(Role.USER);

        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setName("Chris");
        response.setEmail("chris123@gmail.com");
        response.setRole(Role.USER);
        response.setProfileImageUrl(null);

        when(userService.createUser(request)).thenReturn(response);

        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("Chris");
        request.setEmail("chris123@gmail.com");
        request.setProfileImageUrl(null);

        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setName("Chris");
        response.setEmail("chris123@gmail.com");
        response.setRole(Role.USER);
        response.setProfileImageUrl(null);

        when(userService.updateUser(1L,request)).thenReturn(response);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldDeleteUser() throws Exception {

        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk());
    }
}
