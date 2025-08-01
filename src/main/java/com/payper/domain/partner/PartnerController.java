package com.payper.domain.partner;

import com.payper.domain.partner.dto.PartnerKeywordSearchRequest;
import com.payper.domain.partner.dto.PartnerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/partners")
public class PartnerController {
    private final PartnerService partnerService;

    @GetMapping("")
    public ResponseEntity<Map<String,List<PartnerResponse>>> searchPlaces(PartnerKeywordSearchRequest request) {
        List<PartnerResponse> partners = partnerService.findNearbyPartnerResponse(request);
        return ResponseEntity.ok(Map.of("partners", partners));
    }
}
