package com.payper.category.dto;

import com.payper.card.dto.CardResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerResponse {
    private int id;
    private String name;
    private PositionResponse position;
    private List<CardResponse> myCards;
}

