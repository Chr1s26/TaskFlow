package com.project.taskFlow.service.task;

import com.project.taskFlow.dto.TaskCreateRequest;
import com.project.taskFlow.dto.TaskResponse;
import com.project.taskFlow.model.constant.Priority;
import com.project.taskFlow.model.constant.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    public TaskResponse createTask(TaskCreateRequest request);
    public TaskResponse updateTask(Long id, TaskCreateRequest request);
    public void deleteTask(Long id);
    public Page<TaskResponse> getTasks(TaskStatus status, Priority priority, Pageable pageable);
}
