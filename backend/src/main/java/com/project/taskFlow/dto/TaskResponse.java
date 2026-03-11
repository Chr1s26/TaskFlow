package com.project.taskFlow.dto;

import com.project.taskFlow.model.constant.Priority;
import com.project.taskFlow.model.constant.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus taskStatus;
    private Priority priority;
    private LocalDateTime dueDate;
}
