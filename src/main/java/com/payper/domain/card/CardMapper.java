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
    CardResponse selectCardById(int cardId);

    List<CardResponse> searchWithConditions(
            @Param("name") String name,
            @Param("type") String type,
            @Param("category") List<String> category,
            @Param("cardCompany") List<String> cardCompany
    );

    List<CardResponse> selectCardsByUserID(int userId);

    void registerCardMe(@Param("userId") Integer userId, @Param("cardId") Integer cardId);

    boolean existsCardCompany(@Param("cardCompanyName") String cardCompanyName);

    int getCardCompanyId(@Param("cardCompanyName")String cardCompanyName);

    int registerCardCompany(@Param("cardCompanyName")String cardCompanyName);

    int registerCard(@Param("card") RegisterCardRequest card,@Param("cardCompanyId")int cardCompanyId);

    int updateCard(@Param("card") UpdateCardRequest card, @Param("cardCompanyId")int cardCompanyId, @Param("cardId")int cardId);

    int restoreUserCard(@Param("userId") Integer userId, @Param("cardId") Integer cardId);

    boolean isPreviouslyDeletedUserCard(@Param("userId") Integer userId, @Param("cardId") Integer cardId);

    boolean existsById(Integer cardId);

    boolean existsUserCard(@Param("userId") Integer userId, @Param("cardId") Integer cardId);

    int softDeleteMyCard(@Param("userId") Integer userId, @Param("cardId") Integer cardId);

    int softDeleteCard(  @Param("cardId") Integer cardId  );

    List<CardResponse> findByPartnerId(@Param("userId") Integer userId,
                                       @Param("partnerId") Integer partnerId);
}
