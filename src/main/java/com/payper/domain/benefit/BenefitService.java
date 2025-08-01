package com.payper.domain.benefit;

import com.payper.domain.benefit.dto.BenefitsResponse;
import com.payper.domain.benefit.dto.CreateBenefitRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BenefitService {

    private final BenefitMapper benefitMapper;

    @Transactional(readOnly = true)
    public BenefitsResponse findAllByCardId(Integer cardId) {
        return new BenefitsResponse(benefitMapper.findAllByCardId(cardId));
    }

    public void createBenefit(CreateBenefitRequest request) {
        benefitMapper.createBenefit(request);
    }
}
