package com.payper.partner.controller;

import com.payper.partner.dto.PartnerKeywordSearchRequest;
import com.payper.partner.dto.PartnerKeywordSearchResponse;
import com.payper.partner.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
