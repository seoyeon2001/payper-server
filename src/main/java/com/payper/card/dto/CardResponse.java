package com.payper.card.dto;

import java.util.List;

import com.payper.benefit.dto.BenefitResponse;
import lombok.Data;

@Data
public class CardResponse{
	private int id;
	private String name;
	private String type;
	private String imageUrl;
	private CardCompanyResponse company;
	private List<BenefitResponse> benefit;
	private AnnualCost annualCost;
	private List<GradeResponse> grade;
	private String cardIssueUrl;
}