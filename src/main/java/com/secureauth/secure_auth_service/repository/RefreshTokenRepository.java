package com.secureauth.secure_auth_service.repository;

import com.secureauth.secure_auth_service.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    /*
     * Find token using hashed value.
     */
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /*
     * Delete all tokens of a user.
     */
    void deleteByUserId(Long userId);

    /*
     * Delete all devices of one family.
     */
    void deleteByFamilyId(UUID familyId);

    @Modifying
    @Query("""
            UPDATE RefreshToken r
               SET r.revoked = true
             WHERE r.familyId = :familyId
            """)
    int revokeEntireFamily(
            @Param("familyId") UUID familyId);

}