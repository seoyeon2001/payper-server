package com.payper.domain.user.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class NoSuchUserException extends CustomException {
    public NoSuchUserException() {
        super(HttpStatus.NOT_FOUND, "해당 유저가 존재하지 않습니다.");
    }
}
