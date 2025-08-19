package com.payper.domain.card.service;

import com.payper.domain.benefit.domain.Benefit;
import com.payper.domain.benefit.dto.response.BenefitResponse;
import com.payper.domain.card.dto.SearchOptions;
import com.payper.domain.card.mapper.SearchMapper;
import com.payper.domain.card.domain.Card;
import com.payper.domain.card.dto.response.CardCompanyResponse;
import com.payper.domain.card.dto.response.CardResponse;
import com.payper.domain.card.dto.response.CardsResponse;
import com.payper.domain.category.domain.BenefitCategory;
import com.payper.domain.category.dto.CategoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchService {
    private final SearchMapper searchMapper;

    @Transactional
    @Cacheable(
            value = "cardSearch",
            key="#searchOptions.cardType+'_'+#searchOptions.page",
            condition = "(#searchOptions.keyword==null || #searchOptions.keyword=='') && " +
                    "(#searchOptions.cardCompanyNames==null || #searchOptions.cardCompanyNames.size()==0) && " +
                    "(#searchOptions.categoryNames==null || #searchOptions.categoryNames.size()==0)",
            unless = "#result == null"
    )
    public CardsResponse searchCardsPhase1(SearchOptions searchOptions) {
        //조건에 맞는 카드 get
        List<Card> cards=searchMapper.getCardsWithConditionsSliced(searchOptions);

        //Card domain을 Card DTO로 바꾸고 반환
        List<CardResponse> cardResponses = getCardResponsesByCards(cards);

        if(cardResponses.size()<searchOptions.getLimit()){
            return new CardsResponse(
                    false,
                    null,
                    cardResponses
            );
        }
        else{
            return new CardsResponse(
                    true,
                    (searchOptions.getPage()+searchOptions.getLimit())/searchOptions.getLimit(),
                    cardResponses
            );
        }
    }

    //Card domain들을 통해 Card DTO들 생성 후 반환
    private List<CardResponse> getCardResponsesByCards(List<Card> cards) {
        List<CardResponse> cardResponses=new ArrayList<>();

        for(Card card:cards){
            //Card DTO 생성을 위한 Benefit DTO들 생성
            List<BenefitResponse> benefitResponses=getBenefitResponsesByCard(card);

            //Card DTO 생성을 위한 CardCompany DTO 생성
            CardCompanyResponse cardCompanyResponse=CardCompanyResponse.toDTO(
                    card.getCardCompany()
            );

            //get한 데이터들로 Card DTO 생성
            CardResponse cardResponse=CardResponse.toDTO(card,cardCompanyResponse,benefitResponses);

            cardResponses.add(cardResponse);
        }

        return cardResponses;
    }

    //Card domain을 통해 Benefit DTO들 생성 후 반환
    private List<BenefitResponse> getBenefitResponsesByCard(Card card) {
        List<Benefit> benefits = card.getBenefitList();

        List<BenefitResponse> benefitResponses=new ArrayList<>();

        for(Benefit benefit:benefits){
            //Benefit DTO 생성을 위한 Category DTO들 생성
            List<CategoryResponse> categoryResponses=getCategoryResponsesByBenefit(benefit);

            //get한 데이터들로 Benefit DTO생성
            BenefitResponse benefitResponse = BenefitResponse.toDTO(benefit,categoryResponses);

            benefitResponses.add(benefitResponse);
        }

        return benefitResponses;
    }

    //Benefit domain을 통해 Category DTO들 생성 후 반환
    private List<CategoryResponse> getCategoryResponsesByBenefit(Benefit benefit) {
        List<CategoryResponse> categoryResponses=new ArrayList<>();

        List<BenefitCategory> benefitCategories = benefit.getBenefitCategoryList();

        for(BenefitCategory benefitCategory:benefitCategories){
            //BenefitCategory domain을 통해 Category DTO 생성
            CategoryResponse categoryResponse=CategoryResponse.toDTO(
                    benefitCategory.getCategory()
            );

            categoryResponses.add(categoryResponse);
        }

        return categoryResponses;
    }
}
