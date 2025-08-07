package com.payper.external.crawling.service;

import com.payper.domain.benefit.BenefitMapper;
import com.payper.domain.benefit.dto.request.CreateBenefitRequest;
import com.payper.domain.card.CardMapper;
import com.payper.domain.card.dto.RegisterCardRequest;
import com.payper.domain.category.CategoryMapper;
import com.payper.domain.partner.PartnerMapper;
import com.payper.external.crawling.dto.Benefit;
import com.payper.external.crawling.dto.CardData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.payper.external.crawling.config.CardDataMatcher.*;

@Service
@RequiredArgsConstructor
public class CardSaveService {

    private final CardMapper cardMapper;
    private final BenefitMapper benefitMapper;
    private final CategoryMapper categoryMapper;
    private final PartnerMapper partnerMapper;

    @Transactional
    public void saveFullCardData(CardData cardData) {
        RegisterCardRequest cardRequest = toRegisterCardRequest(cardData);

        // 카드사
        Integer companyId = saveCardCompany(cardData.getCompanyName());

        // 카드
        Integer cardId = cardMapper.getId(cardRequest.getCardName());
        if(cardId != null) return;
        cardId = saveCard(cardRequest, companyId);

        for(Benefit benefit : cardData.getBenefits()) {
            // 혜택
            CreateBenefitRequest benefitRequest = toCreateBenefitRequest(benefit);
            Integer benefitId = saveBenefit(benefitRequest, cardId);

            // 혜택_가맹점, 혜택_카테고리
            for(Integer partnerId : benefit.getPartnerIds()){
                saveBenefitPartner(benefitId, partnerId);
            }
            for(Integer categoryId : benefit.getCategoryIds()){
                saveBenefitCategory(benefitId, categoryId);
            }
        }
    }

    // 카드사 등록
    private Integer saveCardCompany(String companyName) {
        Integer companyId = cardMapper.getCompanyId(companyName);
        if (companyId == null) {
            cardMapper.registerCompany(companyName);
            companyId = cardMapper.getCompanyId(companyName);
        }
        return companyId;
    }

    // 카드 등록
    private Integer saveCard(RegisterCardRequest request,
                             Integer companyId) {
        cardMapper.register(request, companyId);
        return request.getCardId();
    }

    // 혜택 등록
    private Integer saveBenefit(CreateBenefitRequest request, Integer cardId){
        benefitMapper.save(cardId, request);
        return request.getBenefitId();
    }

    // 혜택_카테고리 등록
    private void saveBenefitCategory(Integer benefitId, Integer categoryId){
        categoryMapper.registerBenefitCategory(benefitId, categoryId);
    }

    // 혜택_가맹점 등록
    private void saveBenefitPartner(Integer benefitId, Integer partnerId){
        partnerMapper.registerBenefitPartner(benefitId, partnerId);
    }
}
