package com.secureauth.secure_auth_service.service;

import com.secureauth.secure_auth_service.dto.request.LoginRequest;
import com.secureauth.secure_auth_service.dto.request.RefreshTokenRequest;
import com.secureauth.secure_auth_service.dto.request.RegisterRequest;
import com.secureauth.secure_auth_service.dto.response.LoginResponse;
import com.secureauth.secure_auth_service.entity.RefreshToken;
import com.secureauth.secure_auth_service.entity.Users;
import com.secureauth.secure_auth_service.exception.EmailAlreadyExistsException;
import com.secureauth.secure_auth_service.exception.InvalidCredentialsException;
import com.secureauth.secure_auth_service.repository.RefreshTokenRepository;
import com.secureauth.secure_auth_service.repository.UsersRepository;
import com.secureauth.secure_auth_service.security.jwt.JwtService;
import com.secureauth.secure_auth_service.security.user.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsersRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthServiceImpl(UsersRepository repository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void register(RegisterRequest request) {

        if (repository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        Users user = Users.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                // Hash password before saving
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        repository.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        /*
         * Create an authentication token using
         * the email and password provided by the user.
         *
         * This token is NOT authenticated yet.
         */
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        /*
         * Ask Spring Security to authenticate the user.
         *
         * Internally Spring will:
         *
         * 1. Call CustomUserDetailsService
         * 2. Load user from DB
         * 3. Compare password using BCrypt
         * 4. Throw exception if invalid
         */
        try {

            Authentication authentication = authenticationManager.authenticate(authenticationToken);


            /*
             * After successful authentication,
             * Spring returns the authenticated user.
             */
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            /*
             * Generate JWT.
             */
            String accessToken = jwtService.generateToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            RefreshToken token =
                    RefreshToken.builder()

                            .token(refreshToken)

                            .user(userDetails.getUser())

                            .expiryDate(
                                    LocalDateTime.now()
                                            .plusDays(7)
                            )

                            .build();

            refreshTokenRepository.save(token);

            return LoginResponse.builder()

                    .accessToken(accessToken)

                    .refreshToken(refreshToken)

                    .build();

        } catch (Exception e) {

            throw new InvalidCredentialsException("Invalid email or password");
        }

    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        /*
         * Find refresh token in database.
         */
        RefreshToken refresh =
                refreshTokenRepository
                        .findByToken(request.getRefreshToken())
                        .orElseThrow(() ->
                                new InvalidCredentialsException(
                                        "Invalid refresh token"));

        /*
         * Check whether token is revoked.
         */
        if(refresh.isRevoked()){

            throw new InvalidCredentialsException(
                    "Refresh token revoked");
        }

        /*
         * Load user.
         */
        CustomUserDetails userDetails =
                new CustomUserDetails(
                        refresh.getUser());

        /*
         * Generate new access token.
         */
        String accessToken =
                jwtService.generateToken(userDetails);

        return LoginResponse.builder()

                .accessToken(accessToken)

                .refreshToken(request.getRefreshToken())

                .build();
    }

    @Override
    public void logout(
            Authentication authentication){

        /*
         * Current logged-in user.
         */
        CustomUserDetails user =
                (CustomUserDetails)
                        authentication.getPrincipal();

        /*
         * Remove all refresh tokens.
         */
        refreshTokenRepository
                .deleteByUserId(
                        user.getUser().getId()
                );

    }


}
