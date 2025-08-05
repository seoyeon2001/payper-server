package com.payper.domain.card.dto;

import com.payper.external.crawling.dto.CardData;
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
}
