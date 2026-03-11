package com.project.taskFlow.service;

import com.project.taskFlow.dto.TaskCreateRequest;
import com.project.taskFlow.dto.TaskResponse;
import com.project.taskFlow.exception.ResourceNotFoundException;
import com.project.taskFlow.exception.UnauthorizedException;
import com.project.taskFlow.model.Task;
import com.project.taskFlow.model.User;
import com.project.taskFlow.model.constant.Priority;
import com.project.taskFlow.model.constant.TaskStatus;
import com.project.taskFlow.repository.TaskRepository;
import com.project.taskFlow.service.task.TaskServiceImpl;
import com.project.taskFlow.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void shouldCreateTask() {

        User user = new User();
        user.setId(1L);

        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Test Task");
        request.setDescription("description");
        request.setPriority(Priority.HIGH);
        request.setStatus(TaskStatus.TODO);
        request.setDueDate(LocalDateTime.now());

        when(userService.getCurrentUser()).thenReturn(user);

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle("Test Task");
        savedTask.setStatus(TaskStatus.TODO);
        savedTask.setPriority(Priority.HIGH);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskResponse response = taskService.createTask(request);

        assertEquals("Test Task", response.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldUpdateTaskSuccessfully() {

        User user = new User();
        user.setId(1L);

        Task task = new Task();
        task.setId(1L);
        task.setUser(user);

        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Updated Task");

        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        TaskResponse response = taskService.updateTask(1L, request);

        assertEquals("Updated Task", response.getTitle());
        verify(taskRepository).save(task);
    }

    @Test
    void shouldThrowUnauthorizedWhenUpdatingOtherUsersTask() {

        User currentUser = new User();
        currentUser.setId(1L);

        User owner = new User();
        owner.setId(2L);

        Task task = new Task();
        task.setUser(owner);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(UnauthorizedException.class,
                () -> taskService.updateTask(1L,new TaskCreateRequest()));
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        when(userService.getCurrentUser()).thenReturn(new User());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateTask(1L,new TaskCreateRequest()));
    }

    @Test
    void shouldDeleteTask() {

        User user = new User();
        user.setId(1L);

        Task task = new Task();
        task.setUser(user);

        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L);

        verify(taskRepository).delete(task);
    }

    @Test
    void shouldThrowUnauthorizedWhenDeletingOtherUsersTask() {

        User currentUser = new User();
        currentUser.setId(1L);

        User owner = new User();
        owner.setId(2L);

        Task task = new Task();
        task.setUser(owner);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(UnauthorizedException.class,
                () -> taskService.deleteTask(1L));
    }

    @Test
    void shouldReturnTasksWithoutFilters() {

        User user = new User();
        user.setId(1L);

        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findByUser(eq(user), any(Pageable.class)))
                .thenReturn(new PageImpl<>(java.util.List.of(new Task())));

        var result = taskService.getTasks(null,null,Pageable.unpaged());

        assertNotNull(result);
    }

    @Test
    void shouldReturnTasksWithStatusAndPriority() {

        User user = new User();
        user.setId(1L);

        when(userService.getCurrentUser()).thenReturn(user);

        when(taskRepository.findByUserAndStatusAndPriority(
                eq(user),
                eq(TaskStatus.TODO),
                eq(Priority.HIGH),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(java.util.List.of(new Task())));

        var result = taskService.getTasks(
                TaskStatus.TODO,
                Priority.HIGH,
                Pageable.unpaged());

        assertNotNull(result);
    }

}

