package com.payper.partner.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartnerKeywordSearchRequest {
    private String category; // 카테고리 코드
    private String x;                 // 경도
    private String y;                 // 위도
}