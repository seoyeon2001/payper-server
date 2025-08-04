package com.payper.domain.card.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class CardDeletionFailedException extends CustomException {
    public CardDeletionFailedException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "카드 삭제를 실패했습니다.");
    }
}
