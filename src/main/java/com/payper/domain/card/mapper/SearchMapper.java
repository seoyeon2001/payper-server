package com.payper.domain.card.mapper;

import com.payper.domain.card.dto.SearchOptions;
import com.payper.domain.card.domain.Card;
import com.payper.domain.card.domain.CardCompany;
import com.payper.domain.category.domain.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SearchMapper {
    List<Card> getCardsWithConditions(@Param("options") SearchOptions searchOptions);

    List<Card> getCardsWithConditionsSliced(@Param("options") SearchOptions searchOptions);

    CardCompany getCardCompanyByCardCompanyId(Integer cardCompanyId);

    Category getCategoryByCategoryId(Integer categoryId);
}
