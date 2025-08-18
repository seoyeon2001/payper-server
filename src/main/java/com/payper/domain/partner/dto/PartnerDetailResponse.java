package com.payper.domain.partner.dto;

import com.payper.domain.card.dto.response.CardResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerDetailResponse {
    private Integer id;
    private String name;
    private String imageUrl;
    private String categoryName;
    private List<CardResponse> myCards;

    public static PartnerDetailResponse build(Integer id,
                                               String name,
                                               String imageUrl,
                                               String categoryName,
                                               List<CardResponse> cards){
        return PartnerDetailResponse.builder()
                .id(id)
                .name(name)
                .imageUrl(imageUrl)
                .categoryName(categoryName)
                .myCards(cards)
                .build();
    }
}
