package com.payper.domain.partner;

import com.payper.domain.partner.dto.*;
import com.payper.domain.user.UserService;
import com.payper.global.security.domain.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{partnerId}")
    public ResponseEntity<PartnerResponse> findPartnerById(
            @AuthenticationPrincipal CustomUser customUser,
            @PathVariable Integer partnerId) {
        Integer userId = userService.getUserId(customUser);
        log.info("파트너 상세 조회 : partnerId - {}, userId - {}", partnerId, userId);
        return ResponseEntity.ok(partnerService.findPartnerById(partnerId, userId));
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
