package com.payper.external.codef.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;


public class ConnectedIdMismatchException extends CustomException {
    public ConnectedIdMismatchException() {
        super(HttpStatus.BAD_GATEWAY, "응답 ConnectedId가 기존 값과 일치하지 않습니다.");
    }
}
