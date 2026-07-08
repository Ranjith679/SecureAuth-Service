package com.secureauth.secure_auth_service.security.token;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

/*
 * Generates cryptographically secure
 * random refresh tokens.
 */
@Component
public class TokenGenerator {

    /*
     * SecureRandom is much stronger
     * than java.util.Random.
     */
    private final SecureRandom secureRandom =
            new SecureRandom();

    /*
     * Creates 64-byte random token.
     */
    public String generateRefreshToken() {

        byte[] bytes = new byte[64];

        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

    }

}