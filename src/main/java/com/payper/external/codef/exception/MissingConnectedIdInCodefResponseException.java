package com.payper.external.codef.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class MissingConnectedIdInCodefResponseException extends CustomException {
    public MissingConnectedIdInCodefResponseException() {
        super(HttpStatus.BAD_GATEWAY, "CODEF에서 성공을 응답했으나 connectedId가 누락되었습니다.");
    }
}
