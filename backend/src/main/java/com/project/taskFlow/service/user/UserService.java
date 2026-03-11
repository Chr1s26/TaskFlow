package com.project.taskFlow.service.user;

import com.project.taskFlow.dto.UserCreateRequest;
import com.project.taskFlow.dto.UserResponse;
import com.project.taskFlow.dto.UserUpdateRequest;
import com.project.taskFlow.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    public UserResponse createUser(UserCreateRequest request);
    public UserResponse updateUser(Long id, UserUpdateRequest request);
    public void deleteUser(Long id);
    public Page<UserResponse> getUsers(Pageable pageable);
    public UserResponse getCurrentUserResponse();
    public User getCurrentUser();
}
