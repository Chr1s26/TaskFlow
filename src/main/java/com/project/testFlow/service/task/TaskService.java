package com.project.testFlow.service.task;

import com.project.testFlow.dto.TaskCreateRequest;
import com.project.testFlow.dto.TaskResponse;
import com.project.testFlow.model.constant.Priority;
import com.project.testFlow.model.constant.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    public TaskResponse createTask(TaskCreateRequest request);
    public TaskResponse updateTask(Long id, TaskCreateRequest request);
    public void deleteTask(Long id);
    public Page<TaskResponse> getTask(TaskStatus status, Priority priority, Pageable pageable);
}
