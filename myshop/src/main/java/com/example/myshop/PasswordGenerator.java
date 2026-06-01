package com.example.myshop;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {

        String password =
                new BCryptPasswordEncoder()
                        .encode("123456");

        System.out.println(password);
    }
}
