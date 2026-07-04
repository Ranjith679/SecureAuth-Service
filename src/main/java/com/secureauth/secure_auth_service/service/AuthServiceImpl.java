package com.secureauth.secure_auth_service.service;

import com.secureauth.secure_auth_service.dto.request.RegisterRequest;
import com.secureauth.secure_auth_service.entity.Users;
import com.secureauth.secure_auth_service.repository.UsersRepository;
import com.secureauth.secure_auth_service.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsersRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UsersRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void register(RegisterRequest request) {

        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
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
}
