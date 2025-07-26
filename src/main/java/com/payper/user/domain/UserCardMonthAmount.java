package com.payper.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCardMonthAmount {
    private Integer userCardMonthAmountId;
    private Integer userCardId;
    private String month;
    private Long totalAmount;
}