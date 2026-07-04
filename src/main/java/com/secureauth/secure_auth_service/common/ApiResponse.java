package com.secureauth.secure_auth_service.common;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    /*
     * Indicates whether API call succeeded.
     */
    private boolean success;

    /*
     * Human readable message.
     */
    private String message;

    /*
     * Actual response data.
     */
    private T data;

}
