package com.payper.benefit.dto;

import java.util.List;

import lombok.Data;

@Data
public class BenefitResponse {
	private int id;
	private String title;
	private String summary;
	private String description;
	private String iconUrl;
	private Limit limit;
	private List<BenefitGradeResponse> benefitGrade;
	private List<CategoryResponse> category;
	private List<PartnerResponse> partner;
	private int minPayment;
}