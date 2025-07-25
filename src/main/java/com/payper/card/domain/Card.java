package com.payper.card.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {
    private Long cardId;

    private Long companyId;

    private String cardName;

    private String cardType;

    private String cardImageUrl;

    private String cardIssueUrl;
}