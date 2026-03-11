package com.project.taskFlow.security.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String jwtToken;
    private String username;
    private String role;

    public UserInfoResponse(Long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
}
