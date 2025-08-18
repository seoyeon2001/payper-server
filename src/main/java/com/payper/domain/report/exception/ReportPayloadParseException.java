package com.payper.domain.report.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ReportPayloadParseException extends CustomException {
    public ReportPayloadParseException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "리포트 payload를 파싱할 수 없습니다.");
    }
}