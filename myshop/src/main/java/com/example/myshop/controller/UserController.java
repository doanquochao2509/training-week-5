package com.example.myshop.controller;

import com.example.myshop.dto.common.ApiResponse;
import com.example.myshop.dto.user.CreateUserRequest;
import com.example.myshop.dto.user.ResetPasswordRequest;
import com.example.myshop.dto.user.UserResponse;
import com.example.myshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> createUser(
            @RequestBody CreateUserRequest request) {

        return ApiResponse.<UserResponse>builder()
                .status(201)
                .message("Tạo tài khoản thành công")
                .data(userService.createUser(request))
                .build();
    }

    @GetMapping
    public ApiResponse<Page<UserResponse>> getUsers(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return ApiResponse.<Page<UserResponse>>builder()
                .status(200)
                .message("Lấy danh sách thành công")
                .data(userService.getUsers(page, size))
                .build();
    }

    @PostMapping("/{id}")
    public ApiResponse<Object> disableUser(
            @PathVariable UUID id) {

        userService.disableUser(id);

        return ApiResponse.builder()
                .status(200)
                .message("Khóa tài khoản thành công")
                .data(null)
                .build();
    }

    @PutMapping("/{id}/reset-password")
    public ApiResponse<Object> resetPassword(
            @PathVariable UUID id,
            @RequestBody ResetPasswordRequest request) {

        userService.resetPassword(
                id,
                request.getNewPassword());

        return ApiResponse.builder()
                .status(200)
                .message("Đặt lại mật khẩu thành công")
                .data(null)
                .build();
    }
}