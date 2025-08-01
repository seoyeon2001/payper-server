package com.payper.domain.partner.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class PartnerKeywordSearchResponse {
    private List<Document> documents;

    @Getter
    public static class Document {
        private String category_name;
        private String place_name;
        private String road_address_name;
        private String place_url;
        private String x;
        private String y;
        private String distance;
    }
}
