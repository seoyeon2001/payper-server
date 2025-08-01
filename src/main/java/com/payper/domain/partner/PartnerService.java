package com.payper.domain.partner;

import com.payper.domain.partner.dto.PartnerKeywordSearchRequest;
import com.payper.domain.partner.dto.PartnerKeywordSearchResponse;
import com.payper.domain.partner.dto.PartnerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerService {
    @Value("${kakao.map.key}")
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

    public List<PartnerResponse> findNearbyPartnerResponse(PartnerKeywordSearchRequest request) {
        String keyword = request.getQuery();
        PartnerKeywordSearchResponse response = findNearbyKeyword(request);

        return response.getDocuments().stream()
                .map(document -> {
                    PartnerResponse.Position position = PartnerResponse.Position.builder()
                            .x(document.getX())
                            .y(document.getY())
                            .distance(Integer.parseInt(document.getDistance()))
                            .place_name(document.getPlace_name())
                            .road_address_name(document.getRoad_address_name())
                            .place_url(document.getPlace_url())
                            .build();


                    return PartnerResponse.builder()
                            .id(null) // keyword의 카테고리 혹은 가맹점 ID
                            .name(keyword)
                            .position(position)
                            .cardResponseList(Collections.emptyList()) // 필요 시 매핑
                            .build();
                })
                .collect(Collectors.toList());

    }
}
