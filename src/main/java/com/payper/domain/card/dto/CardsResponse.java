package com.payper.domain.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CardsResponse {
    private List<CardResponse> cards;
}
