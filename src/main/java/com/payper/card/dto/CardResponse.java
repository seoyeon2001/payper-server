package com.payper.card.dto;

import java.util.List;

import com.payper.benefit.dto.BenefitResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardResponse {
	private int id;
	private String name;
	private String type;
	private String imageUrl;
	private CardCompanyResponse company;
	private List<BenefitResponse> benefits;
	private AnnualCostResponse annualCost;
	private List<GradeResponse> grades;
	private String cardIssueUrl;
}