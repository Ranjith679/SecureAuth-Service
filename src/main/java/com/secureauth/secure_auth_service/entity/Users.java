package com.secureauth.secure_auth_service.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User's first name
    @Column(nullable = false)
    private String firstName;

    // User's last name
    @Column(nullable = false)
    private String lastName;

    // Email must be unique
    @Column(nullable = false, unique = true)
    private String email;

    // Never store plain password
    @Column(nullable = false)
    private String password;

    // Used later for email verification
    private boolean enabled = true;

    // Used later to lock suspicious accounts
    private boolean accountLocked = false;

    // LOCAL, GOOGLE, GITHUB (future)
    private String provider = "LOCAL";

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
