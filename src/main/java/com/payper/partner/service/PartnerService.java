package com.payper.partner.service;

import com.payper.partner.dto.PartnerKeywordSearchRequest;
import com.payper.partner.dto.PartnerSearchOption;
import com.payper.partner.dto.PartnerKeywordSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PartnerService {
    @Value("${kakao.api.key}")
    private String apiKey;

    @Value("${kakao.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private PartnerSearchOption getOptionByCategory(String category){
        switch (category){
            case "CS2":
                return new PartnerSearchOption("편의점", 500);
            case "CT1":
                return new PartnerSearchOption("영화관", 2000);
            default:
                return new PartnerSearchOption("", 500);
        }
    }

    public PartnerKeywordSearchResponse findNearbyKeyword(PartnerKeywordSearchRequest request) {
        PartnerSearchOption option = getOptionByCategory(request.getCategory());

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("category_group_code", request.getCategory())
                .queryParam("query", option.getQuery())
                .queryParam("x", request.getX())
                .queryParam("y", request.getY())
                .queryParam("radius", option.getRadius())
                .queryParam("sort", "distance");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);

        HttpEntity<PartnerKeywordSearchResponse> response = restTemplate.exchange(
                builder.build(false).encode().toUri(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                PartnerKeywordSearchResponse.class
        );
        return response.getBody();
    }
}
