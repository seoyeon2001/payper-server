package com.payper.benefit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {
    private Integer gradeId;
    private Integer cardId;
    private Long start;
    private Long end;
    private Long totalDiscount;
    private List<BenefitGrade> gradeBenefitList;
}