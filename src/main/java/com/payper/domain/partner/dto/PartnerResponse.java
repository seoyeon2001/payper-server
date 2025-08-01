package com.payper.domain.partner.dto;

import com.payper.domain.card.domain.Card;
import com.payper.domain.card.dto.CardResponse;
import com.payper.domain.partner.domain.Partner;
import com.payper.global.exception.CustomIllegalArgumentException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerResponse {
    private Integer id;
    private String name;
    private Position position;
    private List<CardResponse> cardResponseList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Position {
        private String x;
        private String y;
        private Integer distance;
        private String place_name;
        private String road_address_name;
        private String place_url;
    }

    public static PartnerResponse toDTO(final Partner partner,
                                             final List<CardResponse> cardResponseList,
                                             final Position position) {
        if (partner == null) {
            throw new CustomIllegalArgumentException("partner");
        }

        return PartnerResponse.builder()
                .id(partner.getPartnerId())
                .name(partner.getPartnerName())
                .position(position)
                .cardResponseList(
                        cardResponseList != null
                        ? cardResponseList : Collections.emptyList()
                )
                .build();
    }

}
