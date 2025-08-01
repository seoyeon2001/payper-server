package com.payper.domain.card.dto;

import java.util.Collections;
import java.util.List;

import com.payper.domain.benefit.dto.BenefitResponse;
import com.payper.domain.benefit.dto.GradeResponse;
import com.payper.domain.card.domain.Card;
import com.payper.domain.card.domain.CardCompany;
import com.payper.domain.card.domain.CardType;
import com.payper.global.exception.CustomIllegalArgumentException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardResponse {
	private Integer id;
	private String name;
	private CardType type;
	private String imageUrl;
	private CardCompanyResponse company;
	private String annualCost;
	private String cardIssueUrl;

	private List<BenefitResponse> benefitResponseList;
	private List<GradeResponse> gradeResponseList;

	public static CardResponse toDTO(Card card,
		CardCompanyResponse cardCompanyResponse,
		List<BenefitResponse> benefitResponseList,
		List<GradeResponse> gradeResponseList) {

		if(card==null){
			throw new CustomIllegalArgumentException("card");
		}
		else if(cardCompanyResponse==null){
			throw new CustomIllegalArgumentException("cardCompanyResponse");
		}

		return CardResponse.builder()
				.id(card.getCardId())
				.name(card.getCardName())
				.type(card.getCardType())
				.imageUrl(card.getCardImageUrl())
				.company(cardCompanyResponse)
				.benefitResponseList(
						benefitResponseList!=null
						?benefitResponseList:Collections.emptyList()
				)
				.annualCost(card.getAnnualFee())
				.gradeResponseList(
						gradeResponseList!=null
						?gradeResponseList:Collections.emptyList()
				)
				.cardIssueUrl(card.getCardIssueUrl())
				.build();
	}
}