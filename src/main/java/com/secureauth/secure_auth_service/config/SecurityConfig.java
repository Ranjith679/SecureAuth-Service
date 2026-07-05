package com.secureauth.secure_auth_service.config;

import com.secureauth.secure_auth_service.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(AuthenticationProvider authenticationProvider,JwtAuthenticationFilter jwtFilter){
        this.authenticationProvider = authenticationProvider;
        this.jwtFilter = jwtFilter;
    }
        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

            return http

                    .csrf(AbstractHttpConfigurer::disable)
                     // server stores no sessions anymore , every request must send JWT
                    .sessionManagement(session->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .authenticationProvider(authenticationProvider)
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                    .authorizeHttpRequests(auth-> auth.requestMatchers("/api/v1/auth/register", "/api/v1/auth/login").permitAll().requestMatchers("/h2-console/**").permitAll().anyRequest().authenticated())
                    .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                    .build();
        }
}
