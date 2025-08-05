package com.payper.domain.card;

import com.payper.domain.card.dto.CardResponse;
import com.payper.domain.card.dto.RegisterCardRequest;
import com.payper.domain.card.dto.UpdateCardRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CardMapper {
    List<CardResponse> selectAllCards();
    CardResponse selectCardById(Integer cardId);

    List<CardResponse> searchWithConditions(
            @Param("name") String name,
            @Param("type") String type,
            @Param("category") List<String> category,
            @Param("cardCompany") List<String> cardCompany
    );

    List<CardResponse> selectCardsByUserID(Integer userId);

    void registerCardMe(@Param("userId") Integer userId, @Param("cardId") Integer cardId);

    boolean existsCardCompany(@Param("cardCompanyName") String cardCompanyName);

    Integer getCardId(String cardName);

    Integer getCardCompanyId(@Param("cardCompanyName")String cardCompanyName);

    Integer registerCardCompany(@Param("cardCompanyName")String cardCompanyName);

    Integer registerCard(@Param("card") RegisterCardRequest card,@Param("cardCompanyId")Integer cardCompanyId);

    Integer updateCard(@Param("card") UpdateCardRequest card, @Param("cardCompanyId")Integer cardCompanyId, @Param("cardId")Integer cardId);

    Integer restoreUserCard(@Param("userId") Integer userId, @Param("cardId") Integer cardId);

    boolean isPreviouslyDeletedUserCard(@Param("userId") Integer userId, @Param("cardId") Integer cardId);

    boolean existedByCardId(Integer cardId);

    boolean existsByCardId(Integer cardId);

    boolean existsUserCard(@Param("userId") Integer userId, @Param("cardId") Integer cardId);

    Integer softDeleteMyCard(@Param("userId") Integer userId, @Param("cardId") Integer cardId);

    Integer softDeleteCard(Integer cardId);

    List<CardResponse> findByPartnerId(@Param("userId") Integer userId,
                                       @Param("partnerId") Integer partnerId);
}
