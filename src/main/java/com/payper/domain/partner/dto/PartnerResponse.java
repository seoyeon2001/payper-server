package com.payper.domain.partner.dto;

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
    private String imageUrl;
    private String categoryName;
    private Position position;
    private List<CardResponse> myCards;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Position {
        private String x;
        private String y;
        private Integer distance;
        private String placeName;
        private String roadAddressName;
        private String placeUrl;
    }

//    public static PartnerResponse toDTO(final Partner partner,
//                                             final List<CardResponse> myCards,
//                                             final Position position) {
//        if (partner == null) {
//            throw new CustomIllegalArgumentException("partner");
//        }
//
//        return PartnerResponse.builder()
//                .id(partner.getPartnerId())
//                .name(partner.getPartnerName())
//                .position(position)
//                .myCards(
//                        myCards != null
//                        ? myCards : Collections.emptyList()
//                )
//                .build();
//    }

    public static PartnerResponse buildPartner(Integer id,
                                        String name,
                                        String imageUrl,
                                        String categoryName,
                                        PartnerResponse.Position position,
                                        List<CardResponse> cards){
        return PartnerResponse.builder()
                .id(id)
                .name(name)
                .imageUrl(imageUrl)
                .categoryName(categoryName)
                .position(position)
                .myCards(cards)
                .build();
    }

    public static PartnerResponse.Position buildPosition(PartnerKeywordSearchResponse.Document doc) {
        return Position.builder()
                .x(doc.getX())
                .y(doc.getY())
                .distance(Integer.parseInt(doc.getDistance()))
                .placeName(doc.getPlaceName())
                .roadAddressName(doc.getRoadAddressName())
                .placeUrl(doc.getPlaceUrl())
                .build();
    }


}
