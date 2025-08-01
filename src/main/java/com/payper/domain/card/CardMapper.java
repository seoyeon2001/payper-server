package com.payper.domain.card;

import com.payper.domain.card.dto.CardResponse;
import com.payper.domain.card.dto.RegisterCardRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CardMapper {
    List<CardResponse> selectAllCards();
    CardResponse selectCardById(int cardId);
    int findById(int cardId);

    List<CardResponse> searchWithConditions(
            @Param("name") String name,
            @Param("type") String type,
            @Param("category") List<String> category,
            @Param("cardCompany") List<String> cardCompany
    );

    List<CardResponse> selectCardsByUserID(int userId);

    void registerCardMe(@Param("cardId") Integer cardId, @Param("userId") Integer userId);

    boolean existsCardCompany(@Param("cardCompanyName") String cardCompanyName);

    int getCardCompanyId(@Param("cardCompanyName")String cardCompanyName);

    int registerCardCompany(@Param("cardCompanyName")String cardCompanyName);

    int registerCard(@Param("card") RegisterCardRequest card,@Param("cardCompanyId")int cardCompanyId);

    int softDeleteCard(@Param("userId") Integer userId, @Param("cardId") Integer cardId);
}
