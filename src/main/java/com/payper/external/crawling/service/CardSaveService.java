package com.payper.external.crawling.service;

import com.payper.domain.benefit.BenefitMapper;
import com.payper.domain.benefit.dto.request.CreateBenefitRequest;
import com.payper.domain.card.CardMapper;
import com.payper.domain.card.dto.RegisterCardRequest;
import com.payper.domain.category.CategoryMapper;
import com.payper.domain.category.dto.RegisterCategoryRequest;
import com.payper.domain.partner.PartnerMapper;
import com.payper.domain.partner.dto.RegisterPartnerRequest;
import com.payper.external.crawling.dto.Benefit;
import com.payper.external.crawling.dto.CardData;
import com.payper.external.crawling.dto.Discount;
import com.payper.external.crawling.dto.Grade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        Integer cardId = cardMapper.getCardId(cardRequest.getCardName());
        if(cardId != null) return;
        cardId = saveCard(cardRequest, companyId);

        // 공통 실적등급
        saveGrade(cardData.getGrades(), cardId);

        for(Benefit benefit : cardData.getBenefits()) {
            // 혜택
            CreateBenefitRequest benefitRequest = toCreateBenefitRequest(benefit);
            Integer benefitId = saveBenefit(benefitRequest, cardId);

            // 카테고리
            RegisterCategoryRequest categoryRequest = toRegisterCategoryRequest(benefit.getTitle());
            Integer categoryId = saveCategory(categoryRequest);

            // 혜택_카테고리
            saveBenefitCategory(benefitId, categoryId);

            // 가맹점
            for(String partner : benefit.getCategories()){
                RegisterPartnerRequest partnerRequest = toRegisterPartnerRequest(partner);
                Integer partnerId = savePartner(partnerRequest, categoryId);
                saveBenefitPartner(benefitId, partnerId);
            }

            // 혜택_등급_할인
            Discount disCount = benefit.getDiscount();
            Integer gradeId = benefitMapper.findGradeIdByCardIdAndStart(cardId, disCount.getGradeStart());
            saveBenefitGradeDiscount(disCount, benefitId, gradeId);
        }
    }

    // 카드사 등록
    private Integer saveCardCompany(String companyName) {
        Integer companyId = cardMapper.getCardCompanyId(companyName);
        if (companyId == null) {
            cardMapper.registerCardCompany(companyName);
            companyId = cardMapper.getCardCompanyId(companyName);
        }
        return companyId;
    }

    // 카드 등록
    private Integer saveCard(RegisterCardRequest request,
                             Integer companyId) {
        cardMapper.registerCard(request, companyId);
        return request.getCardId();
    }

    // 공통 실적등급 등록
    private void saveGrade(List<Grade> grades, Integer cardId) {
        if (grades == null) {
            grades = new ArrayList<>();
        }

        if (!hasZeroStart(grades)) {
            Grade zeroGrade = Grade.builder()
                    .start(0L)
                    .build();
            grades.add(zeroGrade);
        }

        benefitMapper.registerGrades(grades, cardId);
    }

    // 혜택 등록
    private Integer saveBenefit(CreateBenefitRequest request, Integer cardId){
        benefitMapper.createBenefit(cardId, request);
        return request.getBenefitId();
    }

    // 공통 카테고리 등록
    private Integer saveCategory(RegisterCategoryRequest request) {
        Integer categoryId = categoryMapper.findIdByCategoryName(request.getCategoryName());
        if(categoryId == null) {
            categoryMapper.registerCategory(request);
            categoryId = request.getCategoryId();
        }
        return categoryId;
    }

    // 파트너 등록
    private Integer savePartner(RegisterPartnerRequest request, Integer categoryId){
        Integer partnerId = partnerMapper.findIdByPartnerName(request.getPartnerName());
        if(partnerId == null) {
            partnerMapper.registerPartner(request, categoryId);
            partnerId = request.getPartnerId();
        }
        return partnerId;
    }

    // 혜택_카테고리 등록
    private void saveBenefitCategory(Integer benefitId, Integer categoryId){
        categoryMapper.registerBenefitCategory(benefitId, categoryId);
    }

    // 혜택_가맹점 등록
    private void saveBenefitPartner(Integer benefitId, Integer partnerId){
        partnerMapper.registerBenefitPartner(benefitId, partnerId);
    }

    // 혜택_실적_할인 등록
    private void saveBenefitGradeDiscount(Discount discount, Integer benefitId, Integer gradeId){
        benefitMapper.registerBenefitGradeDiscount(discount, benefitId, gradeId);
    }

    // 실적 관련 설명이 없을 경우
    private boolean hasZeroStart(List<Grade> grades) {
        for (Grade g : grades) {
            if (g.getStart() == 0) return true;
        }
        return false;
    }
}
