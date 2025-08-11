package com.payper.external.codef.dto.output;

import com.payper.domain.user.domain.UserCardTransaction;
import com.payper.external.codef.dto.response.ApprovalListResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalInfo {
    private String resUsedDate;               // 사용 일자 (ex: 20250701)
    private String resUsedTime;               // 사용 시간 (ex: 214900)
    private String resCardNo;                 // 마스킹된 카드번호
    private String resCardNo1;                // 또 다른 마스킹 카드번호
    private String resMemberStoreName;        // 가맹점 이름
    private String resUsedAmount;            // 사용 금액
    private String resPaymentType;            // 결제 타입 / "1": 일시불, "2": 할부, "3": 그외
    private String resInstallmentMonth;       // 할부 개월 수
    private String resApprovalNo;             // 승인 번호
    private String resPaymentDueDate;         // 결제 예정일
    private String resMemberStoreCorpNo;      // 가맹점 사업자등록번호
    private String resMemberStoreType;        // 가맹점 업종
    private String resMemberStoreAddr;        // 가맹점 주소
    private String resMemberStoreTelNo;       // 가맹점 전화번호
    private String resAccountCurrency;        // 통화 / KRW: 한국원화, JPY: 일본 엔, USD: 미국 달러, EUR: 유로... ( ISO 4217 코드)
    private String resHomeForeignType;        // 국내외 비교 - "1":국내, "2":해외
    private String resCancelYN;               // 취소 여부 (0: 아님, 1: 취소)
    private String resCancelAmount;           // 취소 금액
    private String resVAT;                   // 부가세
    private String resCashBack;              // 캐시백 금액
    private String resKRWAmt;                 // 원화 환산 금액
    private String resMemberStoreNo;          // 가맹점 번호
    private String resCardName;               // 카드 이름
    private String commStartDate;             // 통신 시작일
    private String commEndDate;               // 통신 종료일

    static public ApprovalInfo fromDomain(UserCardTransaction domain) {
        return builder()
                .resUsedDate(domain.getResUsedDate())
                .resUsedTime(domain.getResUsedTime())
                .resCardNo(domain.getResCardNo())
                .resCardNo1(domain.getResCardNo1())
                .resMemberStoreName(domain.getResMemberStoreName())
                .resUsedAmount(domain.getResUsedAmount())
                .resPaymentType(domain.getResPaymentType())
                .resInstallmentMonth(domain.getResInstallmentMonth())
                .resApprovalNo(domain.getResApprovalNo())
                .resPaymentDueDate(domain.getResPaymentDueDate())
                .resMemberStoreCorpNo(domain.getResMemberStoreCorpNo())
                .resMemberStoreType(domain.getResMemberStoreType())
                .resMemberStoreAddr(domain.getResMemberStoreAddr())
                .resMemberStoreTelNo(domain.getResMemberStoreTelNo())
                .resAccountCurrency(domain.getResAccountCurrency())
                .resHomeForeignType(domain.getResHomeForeignType())
                .resCancelYN(domain.getResCancelYn())
                .resCancelAmount(domain.getResCancelAmount())
                .resVAT(domain.getResVat())
                .resCashBack(domain.getResCashback())
                .resKRWAmt(domain.getResKrwAmt())
                .resMemberStoreNo(domain.getResMemberStoreNo())
                .resCardName(domain.getResCardName())
                .commStartDate(domain.getCommStartDate())
                .commEndDate(domain.getCommEndDate())
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
                        .resVat(resVAT)
                        .resCashback(resCashBack)
                        .resKrwAmt(resKRWAmt)
                        .resCancelYn(resCancelYN)
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
