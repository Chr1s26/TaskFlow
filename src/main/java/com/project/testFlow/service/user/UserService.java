package com.project.testFlow.service.user;

import com.project.testFlow.dto.UserCreateRequest;
import com.project.testFlow.dto.UserResponse;
import com.project.testFlow.dto.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    public UserResponse createUser(UserCreateRequest request);
    public UserResponse updateUser(Long id, UserUpdateRequest request);
    public void deleteUser(Long id);
    public Page<UserResponse> getUsers(Pageable pageable);
    public UserResponse getCurrentUser();
}
