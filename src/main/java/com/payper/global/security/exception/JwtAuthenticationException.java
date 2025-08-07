package com.payper.global.security.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException(Throwable cause) {
        super("Jwt authentication failed.", cause);
    }
}
