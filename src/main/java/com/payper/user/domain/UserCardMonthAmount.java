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
    private Long userCardMonthAmountId;

    private Long userCardId;

    private String month;

    private Long totalAmount;
}