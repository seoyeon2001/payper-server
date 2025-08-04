package com.payper.domain.benefit;

import com.payper.domain.benefit.dto.request.UpdateBenefitRequest;
import com.payper.domain.benefit.dto.response.BenefitsResponse;
import com.payper.domain.benefit.dto.request.CreateBenefitRequest;
import com.payper.domain.benefit.exception.BenefitNotFoundException;
import com.payper.domain.card.CardMapper;
import com.payper.domain.card.exception.CardNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BenefitService {

    private final BenefitMapper benefitMapper;
    private final CardMapper cardMapper;

    @Transactional(readOnly = true)
    public BenefitsResponse findAllByCardId(Integer cardId) {
        return new BenefitsResponse(benefitMapper.findAllByCardId(cardId));
    }

    public void createBenefit(Integer cardId, CreateBenefitRequest request) {
        benefitMapper.createBenefit(cardId, request);
    }

    public void updateBenefit(Integer cardId, Integer benefitId, UpdateBenefitRequest request) {
        if (!cardMapper.existsById(cardId)) {
            throw new CardNotFoundException();
        }
        if (benefitMapper.updateBenefit(benefitId, request) != 1) {
            throw new BenefitNotFoundException(benefitId);
        }
    }
}
