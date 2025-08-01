package com.payper.domain.partner;

import com.payper.domain.category.CategoryMapper;
import com.payper.domain.partner.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerService {
    @Value("${kakao.map.key}")
    private String apiKey;

    @Value("${kakao.map.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private final CategoryMapper categoryMapper;
    private final PartnerMapper partnerMapper;

    private PartnerKeywordSearchResponse findNearbyKeyword(PartnerKeywordSearchRequest request) {

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

    private List<PartnerIdNameDto> findPartnersByQuery(String keyword) {
        // 1. query가 카테고리 이름과 일치하는지 확인
        Integer categoryId = categoryMapper.findIdByCategoryName(keyword);
        if (categoryId != null) {
            return partnerMapper.findAllByCategoryId(categoryId);
        } else {
            // 2. query가 가맹점 이름으로 조회
            PartnerIdNameDto partnerIdNameDto = partnerMapper.findByPartnerName(keyword);
            if (partnerIdNameDto != null) {
                return List.of(partnerIdNameDto);
            }
        }
        return Collections.emptyList();
    }

    public List<PartnerResponse> findNearbyPartnerResponse(PartnerKeywordSearchRequest request) {
        String keyword = request.getQuery();
        PartnerKeywordSearchResponse response = findNearbyKeyword(request);

        List<PartnerResponse> result = new ArrayList<>();

        // DB에서 keyword로 가맹점 후보 조회
        List<PartnerIdNameDto> matchedPartners = findPartnersByQuery(keyword);
        System.out.println(matchedPartners);

        for (PartnerKeywordSearchResponse.Document document : response.getDocuments()) {
            String categoryName = document.getCategory_name();

            // category_name에 keyword가 포함되지 않으면 건너뜀
            if (categoryName == null ||
                    !categoryName.toLowerCase().contains(keyword.toLowerCase()))  {
                continue;
            }

            //
            PartnerIdNameDto partnerIdNameDto = null;
            for (PartnerIdNameDto partner : matchedPartners) {
                if (document.getPlace_name().contains(partner.getPartnerName())) {
                    partnerIdNameDto = partner;
                    break;
                }
            }


            PartnerResponse.Position position = PartnerResponse.Position.builder()
                    .x(document.getX())
                    .y(document.getY())
                    .distance(Integer.parseInt(document.getDistance()))
                    .place_name(document.getPlace_name())
                    .road_address_name(document.getRoad_address_name())
                    .place_url(document.getPlace_url())
                    .build();

            PartnerResponse responseItem = PartnerResponse.builder()
                    .id(partnerIdNameDto != null ? partnerIdNameDto.getPartnerId() : null)
                    .name(partnerIdNameDto != null ? partnerIdNameDto.getPartnerName() : null)
                    .position(position)
                    .cardResponseList(Collections.emptyList())
                    .build();

            result.add(responseItem);
        }
        return result;
    }

    public List<SearchPartnersResponse> searchPartners(String name, List<String> category) {
        return partnerMapper.searchWithConditions(name, category);
    }
}
