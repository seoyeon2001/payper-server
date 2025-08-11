package com.payper.domain.user.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class UserCardTransactionSaveFailedException extends CustomException {
    public UserCardTransactionSaveFailedException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "카드 사용내역 저장을 실패했습니다.");
    }
}
