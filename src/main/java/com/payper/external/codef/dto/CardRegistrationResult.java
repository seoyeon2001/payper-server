package com.payper.external.codef.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// 카드 등록 결과를 담는 DTO
@Data
@AllArgsConstructor
public class CardRegistrationResult {
    private String apiCardName;        // CODEF에서 받은 카드명
    private String matchedCardName;    // DB에서 매칭된 카드명 (있다면)
    private Integer cardId;            // 매칭된 카드 ID (있다면)
    private RegistrationStatus status; // 등록 상태
    private String message;            // 사용자용 메시지
}
