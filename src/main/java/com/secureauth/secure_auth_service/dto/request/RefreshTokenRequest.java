package com.secureauth.secure_auth_service.dto.request;

import lombok.Getter;
import lombok.Setter;

/*
 * Client sends only refresh token.
 */
@Getter
@Setter
public class RefreshTokenRequest {

    private String refreshToken;

}
