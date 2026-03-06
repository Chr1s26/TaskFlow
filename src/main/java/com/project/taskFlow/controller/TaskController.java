package com.project.taskFlow.controller;

import com.project.taskFlow.dto.TaskCreateRequest;
import com.project.taskFlow.dto.TaskResponse;
import com.project.taskFlow.model.constant.Priority;
import com.project.taskFlow.model.constant.TaskStatus;
import com.project.taskFlow.service.task.TaskService;
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
