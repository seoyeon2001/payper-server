package com.payper.domain.partner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import java.util.List;

@Getter
public class PartnerKeywordSearchResponse {
    private List<Document> documents;

    @Getter
    public static class Document {

        @JsonProperty("category_name")
        private String categoryName;

        @JsonProperty("place_name")
        private String placeName;

        @JsonProperty("road_address_name")
        private String roadAddressName;

        @JsonProperty("place_url")
        private String placeUrl;

        private String x;
        private String y;
        private String distance;
    }
}
