package com.payper.domain.benefit.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class BenefitDeletionFailedException extends CustomException {
    public BenefitDeletionFailedException(Integer cardId, Integer benefitId) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, String.format("cardId: %d, benefitId: %d - 혜택 삭제를 실패했습니다.", cardId, benefitId));
    }
}
