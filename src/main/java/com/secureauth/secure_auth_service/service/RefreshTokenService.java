package com.secureauth.secure_auth_service.service;

import com.secureauth.secure_auth_service.dto.request.RefreshTokenRequest;
import com.secureauth.secure_auth_service.dto.response.LoginResponse;
import com.secureauth.secure_auth_service.entity.Users;

public interface RefreshTokenService {

    /*
     * Creates and stores a new refresh token.
     */
    String createRefreshToken(Users user, String deviceName, String ipAddress);

    /*
     * Rotates an existing refresh token.
     */
    LoginResponse rotateRefreshToken(RefreshTokenRequest request);

}