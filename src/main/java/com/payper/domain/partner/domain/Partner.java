package com.payper.domain.partner.domain;

import com.payper.domain.benefit.domain.BenefitPartner;
import com.payper.domain.user.domain.UserCardTransaction;
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
public class Partner {
    private Integer partnerId;
    private Integer categoryId;
    private String partnerName;
    private String partnerImageUrl;

    List<BenefitPartner> benefitPartnerList;
    List<UserCardTransaction> userCardTransactionList;//왜 필요했죠?? 일단 ERD에 맞게 고치긴 했습니다.

    private Boolean isDeleted;
    private Date createdAt;
    private Date deletedAt;
    private Date lastModifiedAt;
}
