package com.project.taskFlow.dto;

import com.project.taskFlow.model.constant.Priority;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskCreateRequest {
    private String title;
    private String description;
    private Priority priority;
    private LocalDateTime dueDate;
}
