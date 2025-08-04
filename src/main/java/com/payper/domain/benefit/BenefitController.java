package com.payper.domain.benefit;

import com.payper.domain.benefit.dto.response.BenefitsResponse;
import com.payper.domain.benefit.dto.request.CreateBenefitRequest;
import com.payper.domain.benefit.dto.request.UpdateBenefitRequest;
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
        benefitService.createBenefit(cardId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{benefitId}")
    public ResponseEntity<Void> updateBenefit(
            @PathVariable(name = "cardId") Integer cardId,
            @PathVariable(name = "benefitId") Integer benefitId,
            @RequestBody UpdateBenefitRequest request) {
        benefitService.updateBenefit(cardId, benefitId, request);
        return ResponseEntity.ok().build();
    }
}
