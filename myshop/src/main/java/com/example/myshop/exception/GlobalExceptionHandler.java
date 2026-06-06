package com.example.myshop.exception;

import com.example.myshop.dto.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(
            BusinessException ex) {

        ApiResponse<Object> response =
                ApiResponse.builder()
                        .status(ex.getStatus())
                        .message(ex.getMessage())
                        .data(null)
                        .build();

        return ResponseEntity
                .status(ex.getStatus())
                .body(response);
    }
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthorizationDeniedException(
            AuthorizationDeniedException ex) {

        ApiResponse<Object> response =
                ApiResponse.builder()
                        .status(403)
                        .message("Bạn không có quyền thực hiện thao tác này")
                        .data(null)
                        .build();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(
            Exception ex) {
        ex.printStackTrace();
        ApiResponse<Object> response =
                ApiResponse.builder()
                        .status(500)
                        .message("Internal server error")
                        .data(null)
                        .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}