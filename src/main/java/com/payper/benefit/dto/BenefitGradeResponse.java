package com.payper.benefit.dto;

import com.payper.card.dto.GradeResponse;
import lombok.Data;

@Data
public class BenefitGradeResponse {
	private int id;
	private GradeResponse grade;
	private Discount discount;
}