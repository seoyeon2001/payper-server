package com.payper.external.codef.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class EncryptPasswordFailedException extends CustomException {
    public EncryptPasswordFailedException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "비밀번호 암호화에 실패했습니다.");
    }
}
