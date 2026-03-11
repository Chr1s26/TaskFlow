package com.project.taskFlow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.taskFlow.dto.TaskCreateRequest;
import com.project.taskFlow.dto.TaskResponse;
import com.project.taskFlow.model.constant.Priority;
import com.project.taskFlow.model.constant.TaskStatus;
import com.project.taskFlow.security.jwt.JwtUtils;
import com.project.taskFlow.security.services.UserDetailsServiceImpl;
import com.project.taskFlow.service.task.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TaskControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    private TaskService taskService;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    @MockitoBean
    private JwtUtils jwtUtils;
    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldGetTasks() throws Exception {
        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setDescription("Testing");
        response.setTitle("Test");
        response.setTaskStatus(TaskStatus.TODO);
        response.setPriority(Priority.HIGH);
        response.setDueDate(LocalDateTime.now().plusDays(2));

        when(taskService.getTasks(null,null,null)).thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateTask() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Test");
        request.setDescription("Testing");
        request.setPriority(Priority.HIGH);
        request.setDueDate(LocalDateTime.now().plusDays(2));

        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setDescription("Testing");
        response.setTitle("Test");
        response.setTaskStatus(TaskStatus.TODO);
        response.setPriority(Priority.HIGH);
        response.setDueDate(LocalDateTime.now().plusDays(2));

        when(taskService.createTask(request)).thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldUpdateTask() throws Exception {
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Test");
        request.setDescription("Testing");
        request.setPriority(Priority.HIGH);
        request.setDueDate(LocalDateTime.now().plusDays(2));

        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setDescription("Testing");
        response.setTitle("Test");
        response.setTaskStatus(TaskStatus.TODO);
        response.setPriority(Priority.HIGH);
        response.setDueDate(LocalDateTime.now().plusDays(2));

        when(taskService.updateTask(1L, request)).thenReturn(response);


        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteTask() throws Exception {

        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isOk());
    }
}
