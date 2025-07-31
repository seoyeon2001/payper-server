package com.payper.domain.card.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class MyCardDeletionFailedException extends CustomException {
    public MyCardDeletionFailedException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "내 카드 삭제를 실패했습니다.");
    }
}
