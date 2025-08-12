package com.payper.domain.user.domain;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCardTransaction {
    private Integer userCardTransactionId;
    private Integer userCardId;
    private Integer partnerId;

    private String resUsedDate;
    private String resUsedTime;
    private String resPaymentDueDate;
    private String commStartDate;
    private String commEndDate;

    private String resCardNo;
    private String resCardNo1;
    private String resCardName;

    private String resUsedAmount;
    private String resPaymentType;
    private String resInstallmentMonth; //할부개월수

    private String resApprovalNo;

    private String resVat;
    private String resCashback;
    private String resKrwAmt;

    private String resCancelYn;
    private String resCancelAmount;

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

    static public UserCardTransaction fromDomain(UserCardTransaction domain) {
        return builder()
                .resUsedDate(domain.getResUsedDate())
                .resUsedTime(domain.getResUsedTime())
                .resPaymentDueDate(domain.getResPaymentDueDate())
                .commStartDate(domain.getCommStartDate())
                .commEndDate(domain.getCommEndDate())

                .resCardNo(domain.getResCardNo())
                .resCardNo1(domain.getResCardNo1())
                .resCardName(domain.getResCardName())

                .resUsedAmount(domain.getResUsedAmount())
                .resPaymentType(domain.getResPaymentType())
                .resInstallmentMonth(domain.getResInstallmentMonth())

                .resApprovalNo(domain.getResApprovalNo())

                .resVat(domain.getResVat())
                .resCashback(domain.getResCashback())
                .resKrwAmt(domain.getResKrwAmt())

                .resCancelYn(domain.getResCancelYn())
                .resCancelAmount(domain.getResCancelAmount())

                .resMemberStoreName(domain.getResMemberStoreName())
                .resMemberStoreCorpNo(domain.getResMemberStoreCorpNo())
                .resMemberStoreType(domain.getResMemberStoreType())
                .resMemberStoreAddr(domain.getResMemberStoreAddr())
                .resMemberStoreTelNo(domain.getResMemberStoreTelNo())
                .resMemberStoreNo(domain.getResMemberStoreNo())

                .resAccountCurrency(domain.getResAccountCurrency())
                .resHomeForeignType(domain.getResHomeForeignType())

                .build();
    }

    public UserCardTransaction toDomain(Integer userCardId, Integer partnerId) {
        return
                UserCardTransaction.builder()
                        .userCardTransactionId(null)
                        .userCardId(userCardId)
                        .partnerId(partnerId)
                        .resUsedDate(resUsedDate)
                        .resUsedTime(resUsedTime)
                        .resPaymentDueDate(resPaymentDueDate)
                        .commStartDate(commStartDate)
                        .commEndDate(commEndDate)
                        .resCardNo(resCardNo)
                        .resCardNo1(resCardNo1)
                        .resCardName(resCardName)
                        .resUsedAmount(resUsedAmount)
                        .resPaymentType(resPaymentType)
                        .resInstallmentMonth(resInstallmentMonth)
                        .resApprovalNo(resApprovalNo)
                        .resVat(resVat)
                        .resCashback(resCashback)
                        .resKrwAmt(resKrwAmt)
                        .resCancelYn(resCancelYn)
                        .resCancelAmount(resCancelAmount)
                        .resMemberStoreName(resMemberStoreName)
                        .resMemberStoreCorpNo(resMemberStoreCorpNo)
                        .resMemberStoreType(resMemberStoreType)
                        .resMemberStoreAddr(resMemberStoreAddr)
                        .resMemberStoreTelNo(resMemberStoreTelNo)
                        .resMemberStoreNo(resMemberStoreNo)
                        .resAccountCurrency(resAccountCurrency)
                        .resHomeForeignType(resHomeForeignType)
                        .build();
    }
}
