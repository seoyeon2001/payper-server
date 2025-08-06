package com.payper.external.codef.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class CodefAccountNotLinkedException extends CustomException {
    public CodefAccountNotLinkedException() {
        super(HttpStatus.BAD_REQUEST, "해당 사용자는 connectedId가 존재하지 않습니다. codef 계정 등록 절차가 필요합니다.");
    }
}
