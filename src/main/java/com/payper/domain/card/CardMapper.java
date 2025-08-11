package com.payper.domain.card;

import com.payper.domain.card.domain.Card;
import com.payper.domain.card.dto.CardResponse;
import com.payper.domain.card.dto.RegisterCardRequest;
import com.payper.domain.card.dto.UpdateCardRequest;
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

    CardResponse selectById(Integer cardId);

    List<CardResponse> selectByPartnerId(
            @Param("userId") Integer userId,
            @Param("partnerId") Integer partnerId
    );

    CardResponse selectOneByPartnerId(@Param("userId") Integer userId, @Param("partnerId") Integer partnerId);

    List<CardResponse> selectCardsByUserID(Integer userId);

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

    Integer registerCompany(@Param("cardCompanyName")String cardCompanyName);

    Integer register(
            @Param("card") RegisterCardRequest card,
            @Param("cardCompanyId")Integer cardCompanyId
    );


    Integer getId(String cardName);

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

    boolean isPreviouslyDeletedUserCard(
            @Param("userId") Integer userId,
            @Param("cardId") Integer cardId
    );

    //  Card 테이블에서만 삭제할 때 사용 == 만료
    Integer softDeactivateCard(Integer cardId);

    //  Card 테이블과 연관된 테이블들 모두에서 삭제할 때 == 우리 서비스에서 아예 제거
    Integer softDelete(Integer cardId);

    Integer softDeleteMy(
            @Param("userId") Integer userId,
            @Param("cardId") Integer cardId
    );

    Integer softDeleteUserCard(Integer cardId);

    Integer softDeleteBenefit(Integer cardId);
}
