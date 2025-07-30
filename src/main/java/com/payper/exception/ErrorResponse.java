package com.payper.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private String message; // 오류 설명
    private String path;    // 요청 URL

    public static ResponseEntity<ErrorResponse> build(HttpStatus status, String message, String path) {
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.builder()
                        .message(message)
                        .path(path)
                        .build());
    }
}