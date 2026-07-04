package com.secureauth.secure_auth_service.controller;

import com.secureauth.secure_auth_service.dto.request.RegisterRequest;
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
    public String register(@Valid @RequestBody RegisterRequest request){

        authService.register(request);

        return "User registered successfully";
    }
    // Notice @Valid—this triggers Bean Validation before the service runs.
}