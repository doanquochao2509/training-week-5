package com.example.myshop.controller;

import com.example.myshop.dto.*;
import com.example.myshop.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @RequestBody LoginRequest request) {

        LoginResponse response =
                authService.login(request);

        return ApiResponse.<LoginResponse>builder()
                .status(200)
                .message("Đăng nhập thành công")
                .data(response)
                .build();
    }
}
