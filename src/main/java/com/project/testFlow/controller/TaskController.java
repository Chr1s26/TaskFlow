package com.project.testFlow.controller;

import com.project.testFlow.dto.TaskCreateRequest;
import com.project.testFlow.dto.TaskResponse;
import com.project.testFlow.model.constant.Priority;
import com.project.testFlow.model.constant.TaskStatus;
import com.project.testFlow.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<?> getTasks(@RequestParam(required = false) Priority priority,
                                      @RequestParam(required = false)TaskStatus status,
                                      Pageable pageable){
        Page<TaskResponse> tasks = taskService.getTasks(status,priority,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskCreateRequest request){
        TaskResponse taskResponse = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody TaskCreateRequest request){
        TaskResponse taskResponse = taskService.updateTask(id,request);
        return ResponseEntity.status(HttpStatus.OK).body(taskResponse);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
    }
}
