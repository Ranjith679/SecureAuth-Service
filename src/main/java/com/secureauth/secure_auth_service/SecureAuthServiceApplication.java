package com.secureauth.secure_auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;


@SpringBootApplication
@EnableMethodSecurity // without this annotation spring ignores @PreAuthorize
public class SecureAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureAuthServiceApplication.class, args);
	}

}
