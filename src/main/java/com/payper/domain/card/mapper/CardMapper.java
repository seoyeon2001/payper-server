package com.payper.domain.card.mapper;

import com.payper.domain.card.domain.Card;
import com.payper.domain.card.dto.response.CardResponse;
import com.payper.domain.card.dto.request.RegisterCardRequest;
import com.payper.domain.card.dto.request.UpdateCardRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CardMapper {
    boolean existsCardCompany(@Param("cardCompanyName") String cardCompanyName);

    boolean existedById(Integer cardId);

    boolean existsById(Integer cardId);

    boolean existsUserCard(@Param("userId") Integer userId, @Param("cardId") Integer cardId);

    List<CardResponse> selectAll();

    CardResponse selectById(@Param("cardId")Integer cardId, @Param("userId") Integer userId);

    List<CardResponse> selectByPartnerId(
            @Param("userId") Integer userId,
            @Param("partnerId") Integer partnerId
    );

    CardResponse selectOneByPartnerId(@Param("userId") Integer userId, @Param("partnerId") Integer partnerId);

    List<CardResponse> findByUserId(Integer userId);

    List<CardResponse> searchWithConditions(
            @Param("name") String name,
            @Param("type") String type,
            @Param("category") List<String> category,
            @Param("cardCompany") List<String> cardCompany
    );

    void registerMy(
            @Param("userId") Integer userId,
            @Param("cardId") Integer cardId
    );

    void registerMyCardWithNumber(
            @Param("userId") Integer userId,
            @Param("cardId") Integer cardId,
            @Param("lastNumber") String lastNumber
    );

    void registerCodefCardName(
            @Param("cardId") Integer cardId,
            @Param("apiCardName") String apiCardName
    );

    Integer registerCompany(@Param("cardCompanyName")String cardCompanyName);

    Integer register(
            @Param("card") RegisterCardRequest card,
            @Param("cardCompanyId")Integer cardCompanyId
    );


    Integer getId(String cardName);

    Integer getCardId(String codefCardName);

    Integer getCompanyId(@Param("cardCompanyName")String cardCompanyName);

    Card getByCardName(String cardName);


    Integer update(
            @Param("card") UpdateCardRequest card,
            @Param("cardCompanyId")Integer cardCompanyId,
            @Param("cardId")Integer cardId
    );

    Integer restoreUserCard(
            @Param("userId") Integer userId,
            @Param("cardId") Integer cardId
    );

    Integer restoreUserCardWithNumber(
            @Param("userId") Integer userId,
            @Param("cardId") Integer cardId,
            @Param("lastNumber") String lastNumber
    );

    boolean isPreviouslyDeletedUserCard(
            @Param("userId") Integer userId,
            @Param("cardId") Integer cardId
    );

    //  Card 테이블에서만 삭제할 때 사용 == 만료
    Integer softDeactivateCard(Integer cardId);

    //  Card 테이블과 연관된 테이블들 모두에서 삭제할 때 == 우리 서비스에서 아예 제거
    Integer softDelete(Integer cardId);

    Integer deleteCardMe(
            @Param("userId") Integer userId,
            @Param("cardId") Integer cardId
    );

    Integer softDeleteUserCard(Integer cardId);

    Integer softDeleteBenefit(Integer cardId);
}
