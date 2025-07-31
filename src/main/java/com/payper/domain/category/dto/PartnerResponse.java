package com.payper.domain.category.dto;

import com.payper.domain.card.dto.CardResponse;
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
    private Integer id;
    private String name;
    private PositionResponse position;
    private List<CardResponse> cardList;
}