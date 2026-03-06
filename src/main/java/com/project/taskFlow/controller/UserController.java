package com.project.taskFlow.controller;

import com.project.taskFlow.dto.UserCreateRequest;
import com.project.taskFlow.dto.UserResponse;
import com.project.taskFlow.dto.UserUpdateRequest;
import com.project.taskFlow.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(){
        UserResponse userResponse = userService.getCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @GetMapping
    public ResponseEntity<?> getUsers(Pageable pageable){
        Page<UserResponse> users = userService.getUsers(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserCreateRequest request){
        UserResponse userResponse = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request){
        UserResponse userResponse = userService.updateUser(id,request);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
    }
}
