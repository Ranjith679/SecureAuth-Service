package com.secureauth.secure_auth_service.security.token;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/*
 * Converts refresh token
 * into SHA-256 hash.
 */
@Component
public class TokenHasher {

    public String hash(String token){

        try{

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            token.getBytes(StandardCharsets.UTF_8)
                    );

            return HexFormat.of()
                    .formatHex(hash);

        }catch(Exception ex){

            throw new RuntimeException(ex);

        }

    }

}