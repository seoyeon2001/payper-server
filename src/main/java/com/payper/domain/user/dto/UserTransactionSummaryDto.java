package com.payper.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTransactionSummaryDto {
    private Integer userCardTransactionId;
    private String resMemberStoreName;
    private String resMemberStoreType;
    private String resUsedAmount;
}
