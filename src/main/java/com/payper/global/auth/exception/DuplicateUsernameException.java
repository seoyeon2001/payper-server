package com.payper.global.auth.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class DuplicateUsernameException extends CustomException {
    public DuplicateUsernameException(String username) {
        super(HttpStatus.CONFLICT, "이미 사용 중인 username입니다: " + username);
    }
}
