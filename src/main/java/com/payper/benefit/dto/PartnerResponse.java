package com.payper.benefit.dto;

import com.payper.card.dto.CardResponse;
import lombok.Data;

import java.util.List;

@Data
public class PartnerResponse {
	private int id;
	private String name;
	private List<CardResponse> myCards;
}