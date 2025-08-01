package com.payper.domain.partner;

import com.payper.domain.partner.dto.PartnerKeywordSearchRequest;
import com.payper.domain.partner.dto.PartnerResponse;
import com.payper.domain.partner.dto.SearchPartnersResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/partners")
public class PartnerController {
    private final PartnerService partnerService;

    @GetMapping("")
    public ResponseEntity<Map<String,List<PartnerResponse>>> searchPlaces(PartnerKeywordSearchRequest request) {
        List<PartnerResponse> partners = partnerService.findNearbyPartnerResponse(request);
        return ResponseEntity.ok(Map.of("partners", partners));
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, List<SearchPartnersResponse>>> searchPartners(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<String> category
    ) {
        log.info("검색 파트너 조회");
        List<SearchPartnersResponse> partners = partnerService.searchPartners(name, category);
        return ResponseEntity.ok(Map.of("partners", partners));
    }
}
