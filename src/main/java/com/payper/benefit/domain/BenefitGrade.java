package com.payper.benefit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitGrade {
    private Long benefitGradeId;

    private Long discountId;

    private Long gradeId;

    private Long benefitId;
}