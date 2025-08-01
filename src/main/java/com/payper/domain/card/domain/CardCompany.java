package com.payper.domain.card.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardCompany {
    private Integer companyId;
    private String companyName;

    private List<Card> cardList;
}
