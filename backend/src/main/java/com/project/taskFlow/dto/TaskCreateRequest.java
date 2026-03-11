package com.project.taskFlow.dto;

import com.project.taskFlow.model.constant.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskCreateRequest {
    @NotBlank
    private String title;
    @Size(max = 500)
    private String description;
    @NotNull
    private Priority priority;
    private LocalDateTime dueDate;
}
