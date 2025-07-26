package com.payper.benefit.domain;

import com.payper.category.domain.Category;
import com.payper.category.domain.Partner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Benefit {
    private Integer benefitId;
    private Integer cardId;
    private String benefitTitle;
    private String benefitSummary;
    private String benefitIconUrl;
    private Long minPayment;
    private String benefitDescription;
    private List<Category> cartegoryList; // 카테고리 리스트
    private List<Partner> partnerList; // 파트너 리스트
    private List<BenefitGrade> benefitGradeList;
}