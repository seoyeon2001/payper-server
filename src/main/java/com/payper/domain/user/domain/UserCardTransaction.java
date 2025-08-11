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
public class UserCardTransaction {
    private Integer userCardTransactionId;
    private Integer userCardId;
    private Integer partnerId;// 왜 필요했죠??

    private String resUsedDate;
    private String resUsedTime;
    private String resPaymentDueDate;
    private String commStartDate;
    private String commEndDate;

    private String resCardNo;
    private String resCardNo1;
    private String resCardName;

    private Long resUsedAmount;
    private String resPaymentType;
    private String resInstallmentMonth;//할부개월수

    private String resApprovalNo;

    private Long resVat;
    private Long resCashback;
    private String resKrwAmt;

    private String resCancelYn;
    private Long resCancelAmount;

    private String resMemberStoreName;
    private String resMemberStoreCorpNo;
    private String resMemberStoreType;
    private String resMemberStoreAddr;
    private String resMemberStoreTelNo;
    private String resMemberStoreNo;

    private String resAccountCurrency;
    private String resHomeForeignType;

    private Boolean isDeleted;
    private Date createdAt;
    private Date deletedAt;
    private Date lastModifiedAt;
}