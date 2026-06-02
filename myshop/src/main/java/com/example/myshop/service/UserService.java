package com.example.myshop.service;

import com.example.myshop.dto.user.CreateUserRequest;
import com.example.myshop.dto.user.UserResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);
    Page<UserResponse> getUsers(int page, int size);

    void disableUser(UUID id);

    void resetPassword(UUID id, String password);
}