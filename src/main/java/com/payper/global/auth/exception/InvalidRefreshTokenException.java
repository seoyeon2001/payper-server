package com.payper.global.auth.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends CustomException {
    public InvalidRefreshTokenException(String oldRefreshToken) {
        super(HttpStatus.UNAUTHORIZED, "Invalid refresh token: " + oldRefreshToken);
    }
}
