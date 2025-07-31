package com.payper.domain.partner;

import com.payper.domain.partner.dto.PartnerKeywordSearchRequest;
import com.payper.domain.partner.dto.PartnerKeywordSearchResponse;
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
    @Value("${kakao.client.id}")
    private String apiKey;

    @Value("${kakao.map.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();


    public PartnerKeywordSearchResponse findNearbyKeyword(PartnerKeywordSearchRequest request) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("query", request.getQuery())
                .queryParam("x", request.getX())
                .queryParam("y", request.getY())
                .queryParam("radius", 1000)
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
