package com.example.myshop.service;
import com.example.myshop.dto.LoginRequest;
import com.example.myshop.dto.LoginResponse;
import com.example.myshop.exception.UnauthorizedException;
import com.example.myshop.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.
        AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.
        UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager
            authenticationManager;

    private final JwtService jwtService;

    public LoginResponse login(
            LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));

        } catch (BadCredentialsException ex) {

            throw new UnauthorizedException(
                    "Tên đăng nhập hoặc mật khẩu không đúng");
        }

        String token = jwtService.generateToken(request.getUsername());

        return new LoginResponse(token);
    }
}