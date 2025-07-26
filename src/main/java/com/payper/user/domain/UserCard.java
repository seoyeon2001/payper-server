package com.payper.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCard {
    private Integer userCardId; // userCard id
    private Integer userId; // user id
    private Integer cardId; // card id
    private List<UserCardMonthAmount> userCardMonthAmountList; // 사용자의 월별 카드 사용량 리스트
}