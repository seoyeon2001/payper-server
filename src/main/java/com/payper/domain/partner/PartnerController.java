package com.payper.domain.partner;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/partners")
public class PartnerController {
    private final PartnerService partnerService;

//    @GetMapping("")
//    public PartnerKeywordSearchResponse searchPlaces(PartnerKeywordSearchRequest request) {
//        return partnerService.findNearbyKeyword(request);
//    }
}
