package com.project.testFlow.dto;

import com.project.testFlow.model.constant.Priority;
import com.project.testFlow.model.constant.TaskStatus;
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
