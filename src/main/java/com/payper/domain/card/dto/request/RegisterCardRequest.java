package com.payper.domain.card.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RegisterCardRequest {
    private Integer cardId;
    private String companyName;
    private String cardName;
    private String cardType;
    private String cardImageUrl;
    private String cardIssueUrl;
    private String annualFee;
    private Long prevMonthSpending;
}
