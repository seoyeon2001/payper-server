package com.payper.external.crawling;

import com.payper.domain.card.dto.RegisterCardRequest;
import com.payper.external.crawling.dto.CardData;

public class CardDataMatcher {
    public static RegisterCardRequest toRegisterCardRequest(CardData data) {
        return RegisterCardRequest.builder()
                .companyName(data.getCompanyName())
                .cardName(data.getCardName())
                .cardType(data.getCardType())
                .cardImageUrl(data.getImageUrl())
                .cardIssueUrl(data.getIssueUrl())
                .annualFee(data.getAnnualFee())
                .build();
    }
}
