package com.secureauth.secure_auth_service.security.jwt;

import com.secureauth.secure_auth_service.exception.InvalidCredentialsException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

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

    /**
     * Extract any claim from JWT.
     *
     * This method is generic so we can reuse
     * it for username, expiration, roles etc.
     */
    public <T> T extractClaim(String token, Function<Claims,T> resolver){

        Claims claims = extractAllClaims(token);

        return resolver.apply(claims);

    }

    /**
     * Reads JWT
     * Verifies signature automatically
     * Returns payload (claims)
     */
    private Claims extractAllClaims(String token){

        try{

            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        }catch(JwtException ex){

            throw new InvalidCredentialsException(
                    "Invalid JWT Token"
            );

        }

    }

    /**
     * Returns email stored inside JWT.
     */
    public String extractUsername(String token){

        return extractClaim(
                token,
                Claims::getSubject
        );

    }

    public Date extractExpiration(String token){

        return extractClaim(
                token,
                Claims::getExpiration
        );

    }

    /**
     * Checks whether token has expired.
     */
    private boolean isTokenExpired(String token){

        return extractExpiration(token)
                .before(new Date());

    }

    /**
     * Validates whether JWT belongs
     * to this user and is still valid.
     */
    public boolean isTokenValid(String token, UserDetails userDetails){

        String username = extractUsername(token);

        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);

    }

    /*
     * Creates refresh token.
     * Very similar to access token,
     * but lives much longer.
     */
    public String generateRefreshToken(UserDetails userDetails){

        Date now = new Date();

        Date expiry =
                new Date(now.getTime()+refreshExpiration);

        return Jwts.builder()

                .subject(userDetails.getUsername())

                .issuedAt(now)

                .expiration(expiry)

                .signWith(getSigningKey())

                .compact();

    }

}
