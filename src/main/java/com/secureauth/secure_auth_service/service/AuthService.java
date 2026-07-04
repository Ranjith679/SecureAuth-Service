package com.secureauth.secure_auth_service.service;

import com.secureauth.secure_auth_service.dto.request.LoginRequest;
import com.secureauth.secure_auth_service.dto.request.RegisterRequest;
import com.secureauth.secure_auth_service.dto.response.LoginResponse;

public interface AuthService {

    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

}
