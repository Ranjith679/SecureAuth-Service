package com.secureauth.secure_auth_service.controller;

import com.secureauth.secure_auth_service.common.ApiResponse;
import com.secureauth.secure_auth_service.dto.request.LoginRequest;
import com.secureauth.secure_auth_service.dto.request.RegisterRequest;
import com.secureauth.secure_auth_service.dto.response.LoginResponse;
import com.secureauth.secure_auth_service.security.jwt.JwtService;
import com.secureauth.secure_auth_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService){
        this.authService = authService;
        this.jwtService = jwtService;
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

    // test endpoint for read JWT and return a clain (sub)
    @GetMapping("/token-info")
    public String tokenInfo(@RequestHeader("Authorization") String authHeader){

        String token = authHeader.substring(7);

        return jwtService.extractUsername(token);

    }

    // test endpoint to ensure jwt filter applies before it reaches to endpoint
    @GetMapping("/profile")
    public String profile(){

        return "Welcome User";
    }
}