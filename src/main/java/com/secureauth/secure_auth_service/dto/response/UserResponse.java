package com.secureauth.secure_auth_service.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String role;

}
