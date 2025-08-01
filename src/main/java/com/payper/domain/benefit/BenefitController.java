package com.payper.domain.benefit;

import com.payper.domain.benefit.dto.BenefitsResponse;
import com.payper.domain.benefit.dto.CreateBenefitRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards/{cardId}/benefits")
public class BenefitController {

    private final BenefitService benefitService;

    @GetMapping
    public ResponseEntity<BenefitsResponse> getBenefits(@PathVariable(name = "cardId") Integer cardId) {
        return ResponseEntity.ok(benefitService.findAllByCardId(cardId));
    }

    @PostMapping
    public ResponseEntity<Void> createBenefit(@PathVariable(name = "cardId") Integer cardId, @RequestBody CreateBenefitRequest request) {
        request.setCardId(cardId);
        benefitService.createBenefit(request);
        return ResponseEntity.ok().build();
    }
}
