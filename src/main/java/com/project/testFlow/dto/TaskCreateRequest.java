package com.project.testFlow.dto;

import com.project.testFlow.model.constant.Priority;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskCreateRequest {
    private String title;
    private String description;
    private Priority priority;
    private LocalDateTime dueDate;
}
