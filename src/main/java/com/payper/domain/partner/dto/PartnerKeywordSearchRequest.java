package com.payper.domain.partner.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartnerKeywordSearchRequest {
    private String query;
    private String x;                 // 경도
    private String y;                 // 위도
}