package com.payper.domain.card.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class CardUpdateFailedException extends CustomException {
    public CardUpdateFailedException(Integer cardId) {
        super(HttpStatus.INTERNAL_SERVER_ERROR,String.format("cardId: %d - 해당 카드가 갱신되지 않았습니다", cardId));
    }
}
