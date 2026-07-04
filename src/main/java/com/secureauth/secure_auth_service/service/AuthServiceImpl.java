package com.secureauth.secure_auth_service.service;

import com.secureauth.secure_auth_service.dto.request.LoginRequest;
import com.secureauth.secure_auth_service.dto.request.RegisterRequest;
import com.secureauth.secure_auth_service.dto.response.LoginResponse;
import com.secureauth.secure_auth_service.entity.Users;
import com.secureauth.secure_auth_service.exception.EmailAlreadyExistsException;
import com.secureauth.secure_auth_service.exception.InvalidCredentialsException;
import com.secureauth.secure_auth_service.repository.UsersRepository;
import com.secureauth.secure_auth_service.security.jwt.JwtService;
import com.secureauth.secure_auth_service.security.user.CustomUserDetails;
import com.secureauth.secure_auth_service.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsersRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(UsersRepository repository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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
            String token = jwtService.generateToken(userDetails);

            return new LoginResponse(token);

        } catch (Exception e) {

            throw new InvalidCredentialsException("Invalid email or password");
        }

    }
}
