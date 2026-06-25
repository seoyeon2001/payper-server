package com.payper.global.auth.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends CustomException {
    public InvalidCredentialsException() {
        super(HttpStatus.UNAUTHORIZED, "username 또는 password가 올바르지 않습니다.");
    }
}
