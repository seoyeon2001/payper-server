package com.payper.global.auth.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidAuthRequestException extends CustomException {
    public InvalidAuthRequestException(String fieldName) {
        super(HttpStatus.BAD_REQUEST, fieldName + " 값은 필수입니다.");
    }
}
