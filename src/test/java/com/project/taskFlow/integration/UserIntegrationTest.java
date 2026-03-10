package com.project.taskFlow.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.taskFlow.dto.UserCreateRequest;
import com.project.taskFlow.dto.UserUpdateRequest;
import com.project.taskFlow.model.constant.Role;
import com.project.taskFlow.security.request.LoginRequest;
import com.project.taskFlow.security.request.SignupRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.app.jwtSecret=ThisIsAVeryLongSecretKeyForJwtTestingPurpose123456789",
        "spring.app.jwtExpirationMs=86400000",
        "spring.app.jwtCookieName= cookieName"
})
@AutoConfigureMockMvc
public class UserIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private JavaMailSender javaMailSender;

    private String getAdminToken() throws Exception {

        SignupRequest signup = new SignupRequest();
        signup.setUsername("adminUser");
        signup.setEmail("admin@test.com");
        signup.setPassword("password123");
        signup.setRole("ADMIN");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup)));

        LoginRequest login = new LoginRequest();
        login.setUsername("adminUser");
        login.setPassword("password123");

        String loginResponse = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(loginResponse);

        return json.get("jwtToken").asText();
    }

    private String getUserToken() throws Exception {

        SignupRequest signup = new SignupRequest();
        signup.setUsername("normalUser");
        signup.setEmail("user@test.com");
        signup.setPassword("password123");
        signup.setRole("USER");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup)));

        LoginRequest login = new LoginRequest();
        login.setUsername("normalUser");
        login.setPassword("password123");

        String loginResponse = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(loginResponse);

        return json.get("jwtToken").asText();
    }

    @Test
    void adminShouldCreateUser() throws Exception {

        String token = getAdminToken();

        UserCreateRequest request = new UserCreateRequest();
        request.setName("createdUser");
        request.setEmail("created@test.com");
        request.setPassword("password123");
        request.setRole(Role.USER);

        mockMvc.perform(post("/api/users")
                        .header("Authorization","Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void adminShouldGetUsers() throws Exception {

        String token = getAdminToken();

        mockMvc.perform(get("/api/users")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void userShouldUpdateOwnProfile() throws Exception {

        String token = getUserToken();

        String response = mockMvc.perform(get("/api/users/me")
                        .header("Authorization","Bearer " + token))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        Long userId = json.get("id").asLong();

        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("updatedUser");
        request.setEmail("updated@test.com");

        mockMvc.perform(put("/api/users/" + userId)
                        .header("Authorization","Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void adminShouldDeleteUser() throws Exception {

        String adminToken = getAdminToken();
        String userToken = getUserToken();

        String response = mockMvc.perform(get("/api/users/me")
                        .header("Authorization","Bearer " + userToken))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        Long userId = json.get("id").asLong();

        mockMvc.perform(delete("/api/users/" + userId)
                        .header("Authorization","Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void userShouldNotAccessAdminEndpoints() throws Exception {

        String token = getUserToken();

        mockMvc.perform(get("/api/users")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
