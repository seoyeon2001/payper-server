package com.payper.domain.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCardMonthTransaction {
    private Integer userCardTransactionId;
    private Integer userCardId;
    private Long amount;
    private Date approveAt;

    //private String month; // ex.202507
    //private Long totalAmount; // 누적 금액

    private Boolean isDeleted;
    private Date createdAt;
    private Date deletedAt;
    private Date lastModifiedAt;
}