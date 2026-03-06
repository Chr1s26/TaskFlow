package com.project.taskFlow.service.user;

import com.project.taskFlow.dto.UserCreateRequest;
import com.project.taskFlow.dto.UserResponse;
import com.project.taskFlow.dto.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    public UserResponse createUser(UserCreateRequest request);
    public UserResponse updateUser(Long id, UserUpdateRequest request);
    public void deleteUser(Long id);
    public Page<UserResponse> getUsers(Pageable pageable);
    public UserResponse getCurrentUser();
}
