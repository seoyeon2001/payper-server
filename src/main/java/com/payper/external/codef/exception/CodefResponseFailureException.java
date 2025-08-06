package com.payper.external.codef.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class CodefResponseFailureException extends CustomException {
    public CodefResponseFailureException(String resultCode) {
        super(HttpStatus.BAD_GATEWAY, String.format("CODEF의 요청은 성공했으나 응답 결과 실패입니다. resultCode: %s", resultCode));
    }
}
