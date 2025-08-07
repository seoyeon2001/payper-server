package com.payper.domain.partner.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class PartnerNotFoundException extends CustomException {
    public PartnerNotFoundException(Integer partnerId) {
        super(HttpStatus.NOT_FOUND, "해당하는 ID의 파트너를 찾지 못했습니다. partnerId: " + partnerId);
    }
}
