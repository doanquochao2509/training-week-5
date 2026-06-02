package com.example.myshop.dto.user;

import lombok.Data;

@Data
public class CreateUserRequest {

    private String username;

    private String password;

    private String fullName;

    private String email;

    private String roleCode;
}
