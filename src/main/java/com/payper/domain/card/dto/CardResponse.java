package com.payper.domain.card.dto;

import java.util.Collections;
import java.util.List;

import com.payper.domain.benefit.dto.response.BenefitResponse;
import com.payper.domain.benefit.dto.response.GradeResponse;
import com.payper.domain.card.domain.Card;
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
	private String annualCost;
	private String cardIssueUrl;
	private CardCompanyResponse company;

	private List<BenefitResponse> benefits;
	private List<GradeResponse> grades;

	public static CardResponse toDTO(Card card,
		CardCompanyResponse cardCompanyResponse,
		List<BenefitResponse> benefits,
		List<GradeResponse> grades) {

		if(card == null) {
			throw new CustomIllegalArgumentException("card");
		}
		else if(cardCompanyResponse == null) {
			throw new CustomIllegalArgumentException("cardCompanyResponse");
		}

		return CardResponse.builder()
				.id(card.getCardId())
				.name(card.getCardName())
				.type(card.getCardType())
				.imageUrl(card.getCardImageUrl())
				.annualCost(card.getAnnualFee())
				.cardIssueUrl(card.getCardIssueUrl())
				.company(cardCompanyResponse)
				.benefits(benefits != null ? benefits:Collections.emptyList())
				.grades(grades != null ? grades:Collections.emptyList())
				.build();
	}
}