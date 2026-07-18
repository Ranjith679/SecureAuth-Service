package com.secureauth.secure_auth_service.service;

import com.secureauth.secure_auth_service.dto.request.DeviceInfo;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsersRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final DeviceService deviceService;

    public AuthServiceImpl(UsersRepository repository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, RefreshTokenRepository refreshTokenRepository, RefreshTokenService refreshTokenService, DeviceService deviceService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenService = refreshTokenService;
        this.deviceService = deviceService;
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
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {

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
            DeviceInfo device = deviceService.getDevice(httpRequest);

            /*
             * Generate and store refresh token.
             */
            String refreshToken = refreshTokenService.createRefreshToken(
                    userDetails.getUser(),
                    device.getDeviceName(),
                    device.getIpAddress()
            );

            /*
             * Generate access token (JWT)
             */
            String accessToken = jwtService.generateToken(userDetails);

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

    }


    @Override
    @Transactional
    public void logout(
            Authentication authentication){

        /*
         * Current logged-in user.
         */
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        /*
         * Remove all refresh tokens.
         */
        refreshTokenRepository.deleteByUserId(user.getUser().getId());

    }


}
