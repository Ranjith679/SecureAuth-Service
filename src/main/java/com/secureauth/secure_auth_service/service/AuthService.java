package com.secureauth.secure_auth_service.service;

import com.secureauth.secure_auth_service.dto.request.RegisterRequest;

public interface AuthService {

    void register(RegisterRequest request);

}
