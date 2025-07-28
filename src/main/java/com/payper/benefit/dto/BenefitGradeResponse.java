package com.payper.benefit.dto;

import com.payper.card.dto.GradeResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitGradeResponse {
	private Integer id;
	private GradeResponse grade;
	private DiscountResponse discount;
}