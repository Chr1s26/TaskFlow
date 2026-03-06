package com.project.testFlow.dto;

import com.project.testFlow.model.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    private String name;
    private String email;
    private String password;
    private Role role;
}
