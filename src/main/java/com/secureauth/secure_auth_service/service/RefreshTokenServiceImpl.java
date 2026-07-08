package com.secureauth.secure_auth_service.service;

import com.secureauth.secure_auth_service.dto.request.RefreshTokenRequest;
import com.secureauth.secure_auth_service.dto.response.LoginResponse;
import com.secureauth.secure_auth_service.entity.RefreshToken;
import com.secureauth.secure_auth_service.entity.Users;
import com.secureauth.secure_auth_service.repository.RefreshTokenRepository;
import com.secureauth.secure_auth_service.repository.UsersRepository;
import com.secureauth.secure_auth_service.security.jwt.JwtService;
import com.secureauth.secure_auth_service.security.token.TokenGenerator;
import com.secureauth.secure_auth_service.security.token.TokenHasher;
import com.secureauth.secure_auth_service.security.user.CustomUserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository repository;
    private final TokenGenerator tokenGenerator;
    private final TokenHasher tokenHasher;
    private final JwtService jwtService;
    private final UsersRepository usersRepository;

    public RefreshTokenServiceImpl(RefreshTokenRepository repository, TokenGenerator tokenGenerator, TokenHasher tokenHasher, JwtService jwtService, UsersRepository usersRepository) {
        this.repository = repository;
        this.tokenGenerator = tokenGenerator;
        this.tokenHasher = tokenHasher;
        this.jwtService = jwtService;
        this.usersRepository = usersRepository;
    }

    @Override
    public String createRefreshToken(
            Users user,
            String deviceName,
            String ipAddress){

        /*
         * Generate secure random token.
         */
        String refreshToken = tokenGenerator.generateRefreshToken();

        /*
         * Hash token before saving.
         */
        String hash = tokenHasher.hash(refreshToken);

        RefreshToken entity = RefreshToken.builder()
                        .user(user)
                        .tokenHash(hash)
                        /*
                         * Every login starts a new family.
                         */
                        .familyId(UUID.randomUUID())
                        .deviceName(deviceName)
                        .ipAddress(ipAddress)
                        .createdAt(LocalDateTime.now())
                        .lastUsedAt(LocalDateTime.now())
                        .expiresAt(LocalDateTime.now().plusDays(7))
                        .build();

        repository.save(entity);

        /*
         * Return plain token.
         * Only client should know it.
         */
        return refreshToken;

    }

    @Override
    public LoginResponse rotateRefreshToken(RefreshTokenRequest request) {
        String hash = tokenHasher.hash(request.getRefreshToken());

        RefreshToken refreshToken = repository.findByTokenHash(hash)
                        .orElseThrow(() ->new RuntimeException("Invalid Refresh Token"));

        if(refreshToken.isRevoked()){
            throw new RuntimeException("Refresh Token Revoked");
        }

        if(refreshToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Refresh Token Expired");
        }

        refreshToken.setLastUsedAt(LocalDateTime.now());

        refreshToken.setRevoked(true);

        repository.save(refreshToken);

        String newRefreshToken = tokenGenerator.generateRefreshToken();

        String newHash = tokenHasher.hash(newRefreshToken);

        RefreshToken newEntity = RefreshToken.builder()
                        .user(refreshToken.getUser())
                        /*
                         * Keep same family.
                         */
                        .familyId(refreshToken.getFamilyId())
                        .tokenHash(newHash)
                        .deviceName(refreshToken.getDeviceName())
                        .ipAddress(refreshToken.getIpAddress())
                        .createdAt(LocalDateTime.now())
                        .lastUsedAt(LocalDateTime.now())
                        .expiresAt(LocalDateTime.now().plusDays(7))
                        .build();

        repository.save(newEntity);

        CustomUserDetails userDetails = new CustomUserDetails(refreshToken.getUser());

        String accessToken = jwtService.generateToken(userDetails);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
