package com.payper.domain.benefit.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class BenefitNotFoundException extends CustomException {
    public BenefitNotFoundException(Integer benefitId) {
        super(HttpStatus.NOT_FOUND, String.format("ID가 %d인 혜택을 찾을 수 없습니다.", benefitId));
    }
}
