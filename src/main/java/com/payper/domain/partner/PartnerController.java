package com.payper.domain.partner;

import com.payper.domain.partner.dto.PartnerKeywordSearchRequest;
import com.payper.domain.partner.dto.PartnerResponse;
import com.payper.domain.partner.dto.SearchPartnersResponse;
import com.payper.domain.user.UserService;
import com.payper.global.security.domain.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/partners")
public class PartnerController {
    private final PartnerService partnerService;
    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<Map<String,List<PartnerResponse>>> findPlacesWithMe(
            @AuthenticationPrincipal CustomUser customUser,
            PartnerKeywordSearchRequest request) {
        Integer userId = userService.getUserId(customUser);
        List<PartnerResponse> partners = partnerService.findPlacesWithMe(request, userId);
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
