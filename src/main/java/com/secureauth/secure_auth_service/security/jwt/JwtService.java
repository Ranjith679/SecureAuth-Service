package com.secureauth.secure_auth_service.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Method which returns SecretKey using Keys builder + our secrete string for passing to jwt builder
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Generates a JWT string for the authenticated user.
     */
    public String generateToken(UserDetails userDetails) {

        Date now = new Date();

        Date expiry = new Date(
                now.getTime() + expiration
        );

        return Jwts.builder()

                // Subject = unique identifier (email in our project)
                .subject(userDetails.getUsername())

                // Issued time
                .issuedAt(now)

                // Expiration time
                .expiration(expiry)

                // Sign the JWT
                .signWith(getSigningKey())

                .compact();
    }

}
