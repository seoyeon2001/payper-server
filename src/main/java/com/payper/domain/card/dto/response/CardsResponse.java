package com.payper.domain.card.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CardsResponse {
    private boolean hasNext;
    private Integer page;

    private List<CardResponse> cards;
}
