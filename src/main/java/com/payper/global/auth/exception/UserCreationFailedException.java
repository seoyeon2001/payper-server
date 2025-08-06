package com.payper.global.auth.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class UserCreationFailedException extends CustomException {
    public UserCreationFailedException(String userName) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "유저 생성 실패: " + userName);
    }
}
