package com.payper.card.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {
    private Integer companyId;
    private String companyName;
    private List<Card> cardList; // 회사가 가진 카드 목록
}