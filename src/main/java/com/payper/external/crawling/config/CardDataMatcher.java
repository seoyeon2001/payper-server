package com.payper.external.crawling.config;

import com.payper.domain.benefit.dto.request.CreateBenefitRequest;
import com.payper.domain.card.dto.RegisterCardRequest;
import com.payper.domain.category.dto.RegisterCategoryRequest;
import com.payper.external.crawling.dto.Benefit;
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

    public static CreateBenefitRequest toCreateBenefitRequest(Benefit benefit) {
        return CreateBenefitRequest.builder()
                .title(benefit.getTitle())
                .summary(benefit.getSummary())
                .description(benefit.getDescription())
                .iconUrl("")
                .build();
    }

    public static RegisterCategoryRequest toRegisterCategoryRequest(String categoryName) {
        return RegisterCategoryRequest.builder()
                .categoryName(categoryName)
                .build();
    }
}
