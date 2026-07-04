package com.secureauth.secure_auth_service.controller;

import com.secureauth.secure_auth_service.common.ApiResponse;
import com.secureauth.secure_auth_service.dto.request.LoginRequest;
import com.secureauth.secure_auth_service.dto.request.RegisterRequest;
import com.secureauth.secure_auth_service.dto.response.LoginResponse;
import com.secureauth.secure_auth_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Object> register(@Valid @RequestBody RegisterRequest request){

        authService.register(request);

        return ApiResponse.builder()
                .success(true)
                .message("User registered successfully")
                .data(null)
                .build();

    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request){

        LoginResponse response = authService.login(request);

        return ApiResponse.<LoginResponse>builder()
                .success(true)
                .message("Login successful")
                .data(response)
                .build();

    }
}