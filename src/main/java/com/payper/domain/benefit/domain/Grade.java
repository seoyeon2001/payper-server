package com.payper.domain.benefit.domain;

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
public class Grade {
    private Integer gradeId;
    private Integer cardId;
    private Long start; // 시작 금액 default 0
    private Long end; // 종료 금액 nullable
    private Long totalDiscount; // 등급별 최대 할인 금액 nullable

    private List<BenefitGradeDiscount> benefitGradeDiscountList;

    private Boolean isDeleted;
    private Date createdAt;
    private Date deletedAt;
    private Date lastModifiedAt;
}