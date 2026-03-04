package com.project.testFlow.service.task;

import com.project.testFlow.dto.TaskCreateRequest;
import com.project.testFlow.dto.TaskResponse;
import com.project.testFlow.exception.ResourceNotFoundException;
import com.project.testFlow.exception.UnauthorizedException;
import com.project.testFlow.model.Task;
import com.project.testFlow.model.User;
import com.project.testFlow.model.constant.Priority;
import com.project.testFlow.model.constant.TaskStatus;
import com.project.testFlow.repository.TaskRepository;
import com.project.testFlow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService{

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Override
    public TaskResponse createTask(TaskCreateRequest request) {
        User user = new User();

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(TaskStatus.TODO);
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setCreatedAt(LocalDateTime.now());
        task.setUser(user);

        Task savedTask = taskRepository.save(task);
        return mapToResponse(savedTask);
    }

    @Override
    public TaskResponse updateTask(Long id, TaskCreateRequest request) {
        User user = new User();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if(!task.getUser().getId().equals(user.getId())){
            throw new UnauthorizedException("You cannot update this task");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(task);
    }

    @Override
    public void deleteTask(Long id) {
        User user = new User();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if(!task.getUser().getId().equals(user.getId())){
            throw new UnauthorizedException("You cannot delete this task");
        }
        taskRepository.delete(task);
    }

    @Override
    public Page<TaskResponse> getTask(TaskStatus status, Priority priority, Pageable pageable) {
        return null;
    }

//    private User getCurrentUser(){
//
//    }

    private TaskResponse mapToResponse(Task task){
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setTaskStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setDueDate(task.getDueDate());
        return response;
    }
}
