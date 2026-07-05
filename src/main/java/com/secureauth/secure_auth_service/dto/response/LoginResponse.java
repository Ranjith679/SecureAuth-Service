package com.secureauth.secure_auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginResponse {

    /*
     * Used for API authentication.
     */
    private String accessToken;

    /*
     * Used to obtain a new access token.
     */
    private String refreshToken;


}
