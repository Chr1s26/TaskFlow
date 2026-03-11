package com.project.taskFlow.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.taskFlow.dto.TaskCreateRequest;
import com.project.taskFlow.model.constant.Priority;
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

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.app.jwtSecret=ThisIsAVeryLongSecretKeyForJwtTestingPurpose123456789",
        "spring.app.jwtExpirationMs=86400000",
        "spring.app.jwtCookieName= cookieName"
})
@AutoConfigureMockMvc
public class TaskIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();;

    @MockitoBean
    private JavaMailSender javaMailSender;

    private String getJwtToken() throws Exception {

        SignupRequest signup = new SignupRequest();
        signup.setUsername("taskUser");
        signup.setEmail("task@test.com");
        signup.setPassword("password123");
        signup.setRole("USER");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup)));

        LoginRequest login = new LoginRequest();
        login.setUsername("taskUser");
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
    void shouldCreateTask() throws Exception {

        String token = getJwtToken();

        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Integration Task");
        request.setDescription("Test task");
        request.setPriority(Priority.HIGH);
        request.setDueDate(LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization","Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldGetTasks() throws Exception {

        String token = getJwtToken();

        mockMvc.perform(get("/api/tasks")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateTask() throws Exception {

        String token = getJwtToken();

        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Initial Task");
        request.setDescription("desc");
        request.setPriority(Priority.MEDIUM);
        request.setDueDate(LocalDateTime.now().plusDays(1));

        String createResponse = mockMvc.perform(post("/api/tasks")
                        .header("Authorization","Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(createResponse);
        Long taskId = json.get("id").asLong();

        request.setTitle("Updated Task");

        mockMvc.perform(put("/api/tasks/" + taskId)
                        .header("Authorization","Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteTask() throws Exception {

        String token = getJwtToken();

        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Delete Task");
        request.setDescription("desc");
        request.setPriority(Priority.LOW);
        request.setDueDate(LocalDateTime.now().plusDays(1));

        String createResponse = mockMvc.perform(post("/api/tasks")
                        .header("Authorization","Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(createResponse);
        Long taskId = json.get("id").asLong();

        mockMvc.perform(delete("/api/tasks/" + taskId)
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk());
    }

}
