package com.payper.external.codef.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class MismatchedConnectedIdException extends CustomException {
    public MismatchedConnectedIdException(String connectedId) {
        super(HttpStatus.FORBIDDEN, String.format("응답의 connectedId가 요청한 사용자( %s )와 일치하지 않습니다.", connectedId));
    }
}