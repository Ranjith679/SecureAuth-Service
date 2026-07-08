package com.secureauth.secure_auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/*
 * Enterprise Refresh Token
 *
 * Notice:
 * We DO NOT store the plain refresh token.
 * We only store its hash.
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
     * Actual user.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    /*
     * SHA-256 hash of refresh token.
     * Never store plain token.
     */
    @Column(nullable = false, unique = true)
    private String tokenHash;

    /*
     * Random UUID for token family.
     * Used later for rotation.
     */
    @Column(nullable = false)
    private UUID familyId;

    /*
     * Browser / Mobile / Desktop.
     */
    private String deviceName;

    /*
     * IP Address.
     */
    private String ipAddress;

    /*
     * When created.
     */
    private LocalDateTime createdAt;

    /*
     * Last usage.
     */
    private LocalDateTime lastUsedAt;

    /*
     * Expiration.
     */
    private LocalDateTime expiresAt;

    /*
     * Revoked manually?
     */
    @Builder.Default
    private boolean revoked = false;

}