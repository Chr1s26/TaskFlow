package com.project.taskFlow.service.task;

import com.project.taskFlow.dto.TaskCreateRequest;
import com.project.taskFlow.dto.TaskResponse;
import com.project.taskFlow.exception.ResourceNotFoundException;
import com.project.taskFlow.exception.UnauthorizedException;
import com.project.taskFlow.model.Task;
import com.project.taskFlow.model.User;
import com.project.taskFlow.model.constant.Priority;
import com.project.taskFlow.model.constant.TaskStatus;
import com.project.taskFlow.repository.TaskRepository;
import com.project.taskFlow.repository.UserRepository;
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
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("User Not found"));

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
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("User Not found"));

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
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("User Not found"));

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if(!task.getUser().getId().equals(user.getId())){
            throw new UnauthorizedException("You cannot delete this task");
        }
        taskRepository.delete(task);
    }

    @Override
    public Page<TaskResponse> getTasks(TaskStatus status, Priority priority, Pageable pageable) {
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("User Not found"));

        Page<Task> tasks;

        if(status != null && priority != null){
            tasks = taskRepository.findByUserAndStatusAndPriority(user,status,priority,pageable);
        }else if(status != null){
            tasks = taskRepository.findByUserAndStatus(user,status,pageable);
        }else if(priority != null){
            tasks = taskRepository.findByUserAndPriority(user,priority,pageable);
        }else{
            tasks = taskRepository.findByUser(user,pageable);
        }

        return tasks.map(this::mapToResponse);
    }

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
