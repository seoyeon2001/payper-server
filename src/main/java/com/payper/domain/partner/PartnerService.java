package com.payper.domain.partner;

import com.payper.domain.card.mapper.CardMapper;
import com.payper.domain.card.dto.CardResponse;
import com.payper.domain.category.CategoryMapper;
import com.payper.domain.category.domain.Category;
import com.payper.domain.category.dto.CategoryResponse;
import com.payper.domain.category.exception.CategoryNotFoundException;
import com.payper.domain.partner.domain.Partner;
import com.payper.domain.partner.dto.*;
import com.payper.domain.partner.exception.PartnerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private final RestTemplate restTemplate;

    private final CategoryMapper categoryMapper;
    private final PartnerMapper partnerMapper;
    private final CardMapper cardMapper;

    @Transactional(readOnly = true)
    public List<PartnerResponse> findPlacesWithMe(
            PartnerKeywordSearchRequest request,
            Integer userId) {
        String keyword = request.getQuery();

        // 카카오맵 api에서 가맹점 리스트 조회
        PartnerKeywordSearchResponse response = findNearbyKeyword(request);

        List<PartnerResponse> result = new ArrayList<>();

        // DB에서 keyword로 가맹점 후보 조회
        List<PartnerTempDto> matchedPartners = findPartnersByQuery(keyword);

        // PartnerResponse 값 채우기
        for (PartnerKeywordSearchResponse.Document document : response.getDocuments()) {
            String docCategoryName = document.getCategoryName();

            // category_name에 keyword가 포함되지 않으면 건너뜀
            if (docCategoryName == null ||
                    !docCategoryName.toLowerCase().contains(keyword.toLowerCase()))  {
                continue;
            }

            // DB와 map api 응답객체의 가맹점 match
            PartnerTempDto matchedPartner = matchPartnerFromPlaceName(document.getPlaceName(), matchedPartners);
            Integer partnerId = matchedPartner != null ? matchedPartner.getPartnerId() : null;
            String partnerName = matchedPartner != null ? matchedPartner.getPartnerName() : null;
            String partnerImageUrl = matchedPartner != null ? matchedPartner.getPartnerImageUrl() : null;

            CategoryResponse categoryResponse =
                    partnerId != null ?
                            categoryMapper.findByPartnerId(partnerId) :
                            null;

            //responseItem 생성
            PartnerResponse.Position position = PartnerResponse.buildPosition(document);

            List<CardResponse> myCards = findMyCardsByPartnerId(partnerId, userId);

            PartnerResponse responseItem = PartnerResponse.build(
                    partnerId,
                    partnerName,
                    partnerImageUrl,
                    categoryResponse,
                    position,
                    myCards
                    );

            result.add(responseItem);
        }
        return result;
    }

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

    public List<PartnerTempDto> findPartnersByQuery(String keyword) {
        // 1. query가 카테고리 이름과 일치하는지 확인
        Integer categoryId = categoryMapper.findIdByCategoryName(keyword);
        if (categoryId != null) {
            return partnerMapper.findAllByCategoryId(categoryId);
        }
        // 2. query가 가맹점 이름으로 조회
        PartnerTempDto PartnerTempDto = partnerMapper.findByName(keyword);
        if (PartnerTempDto != null) {
            return List.of(PartnerTempDto);
        }

        return Collections.emptyList();
    }

    public PartnerTempDto matchPartnerFromPlaceName(String placeName, List<PartnerTempDto> candidates){
        for (PartnerTempDto partner : candidates) {
            if (placeName.contains(partner.getPartnerName())) {
                return partner;
            }
        }
        return null;
    }

    private List<CardResponse> findMyCardsByPartnerId(Integer partnerId, Integer userId) {
        return partnerId != null ?
                cardMapper.selectByPartnerId(userId, partnerId) :
                Collections.emptyList();
    }

    public List<SearchPartnersResponse> searchPartners(String name, List<String> category) {
        return partnerMapper.searchWithConditions(name, category);
    }

    public PartnerResponse findPartnerById(Integer partnerId, Integer userId) {
        Partner partner = partnerMapper.findById(partnerId).orElseThrow(() -> new PartnerNotFoundException(partnerId));
        Category category = categoryMapper.findById(partner.getCategoryId()).orElseThrow(() -> new CategoryNotFoundException(partner.getCategoryId()));
        List<CardResponse> myCards = findMyCardsByPartnerId(partnerId, userId);

        return PartnerResponse.build(partner, category, myCards);
    }
}
