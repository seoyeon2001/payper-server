package com.payper.domain.card.domain;

import com.payper.domain.benefit.domain.Benefit;
import com.payper.domain.user.domain.UserCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {
    private Integer cardId;
    private Integer companyId;
    private String cardName;
    private CardType cardType; // enum(credit, check)
    private String cardImageUrl; // 카드 사진 URL
    private String cardIssueUrl; // 카드 상세 URL
    private String annualFee; // 연회비
    private Long prevMonthSpending;

    private List<UserCard> userCardList; // 카드를 가진 사용자 목록
    private List<Benefit> benefitList; // 카드의 혜택 리스트

    private CardCompany cardCompany;

    private Boolean isDeleted;
    private Date createdAt;
    private Date deletedAt;
    private Date lastModifiedAt;
}