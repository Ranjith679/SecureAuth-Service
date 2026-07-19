package com.secureauth.secure_auth_service.security;

/*
 * Represents important authentication
 * security events.
 */
public enum SecurityEvent {

    LOGIN,

    LOGOUT,

    REFRESH,

    TOKEN_REUSE,

    PASSWORD_CHANGED

}
