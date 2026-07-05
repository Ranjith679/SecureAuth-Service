package com.secureauth.secure_auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;


/*
 * Stores refresh tokens in database.
 * Access tokens are NOT stored.
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Refresh token string.
     */
    @Column(nullable = false, unique = true)
    private String token;

    /*
     * Owner of this refresh token.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private Users user;

    /*
     * Expiration date.
     */
    private LocalDateTime expiryDate;

    /*
     * Indicates whether this token
     * has been revoked.
     */
    @Builder.Default
    private boolean revoked = false;

}
