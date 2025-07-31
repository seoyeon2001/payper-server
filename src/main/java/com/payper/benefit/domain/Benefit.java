package com.payper.benefit.domain;

import com.payper.category.domain.BenefitCategory;
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
public class Benefit {
    private Integer benefitId;
    private Integer cardId;
    private String benefitTitle; // 혜택 제목
    private String benefitSummary; // 혜택 간단 설명
    private String benefitDescription; // 혜택 확인 사항
    private String benefitIconUrl;

    private List<BenefitCategory> benefitCategoryList;
    private List<BenefitPartner> benefitPartnerList;
    private List<BenefitGradeDiscount> benefitGradeDiscountList;

    private Boolean isDeleted;
    private Date createdAt;
    private Date deletedAt;
    private Date lastModifiedAt;
}