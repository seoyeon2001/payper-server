package com.payper.card.domain;

import com.payper.benefit.domain.Benefit;
import com.payper.benefit.domain.Grade;
import com.payper.user.domain.UserCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {
    private Integer cardId;
    private Integer companyId;
    private String cardName;
    private String cardType;
    private String cardImageUrl;
    private String cardIssueUrl;
    private List<UserCard> userCardList; // 카드를 가진 사용자 목록
    private List<Grade> gradeList; // 카드의 실적 리스트
    private List<Benefit> benefitList; // 카드의 혜택 리스트
}