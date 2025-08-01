package com.payper.domain.card;

import com.payper.domain.card.dto.CardResponse;
import com.payper.domain.card.dto.RegisterCardMeRequest;
import com.payper.domain.card.dto.RegisterCardRequest;
import com.payper.domain.card.exception.CardNotFoundException;
import com.payper.domain.card.exception.MyCardDeletionFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardService {
    private final CardMapper cardMapper;

    public List<CardResponse> getAllCards() {
        return cardMapper.selectAllCards();
    }

    public CardResponse getCardById(int cardId) {
        existsById(cardId);
        return cardMapper.selectCardById(cardId);
    }

    public void existsById(int cardId) {
        if(cardMapper.findById(cardId)!=1){
            log.error("카드 Not Found - cardId: {}", cardId);
            throw new CardNotFoundException();
        }
    }

    public List<CardResponse> searchCards(String name, String type, List<String> category, List<String> cardCompany) {
        if (name == null && type == null && category == null && cardCompany == null) {
            // 전체 카드 조회
            return cardMapper.selectAllCards();
        }

        // 파라미터에 맞는 조건으로 검색
        return cardMapper.searchWithConditions(name, type, category, cardCompany);
    }

    public List<CardResponse> getAllCardsByMe(Integer userId){
        return cardMapper.selectCardsByUserID(userId);
    }

    public void registerCardMe(RegisterCardMeRequest request, Integer userId) {
        cardMapper.registerCardMe(request.getCardId(), userId);
    }

    @Transactional
    public int registerCard(RegisterCardRequest request){
        int result=0;

        boolean isExists=cardMapper.existsCardCompany(request.getCompanyName());

        try{
            if(!isExists){
                if(cardMapper.registerCardCompany(request.getCompanyName())!=1){
                    throw new RuntimeException("card company register failed");
                }
            }

            int cardCompanyId=cardMapper.getCardCompanyId(request.getCompanyName());
            result=cardMapper.registerCard(request, cardCompanyId);
            if(result!=1){
                throw new RuntimeException("card register failed");
            }
        }
        catch(Exception e){
            throw new RuntimeException(e);//롤백을 위한 예외 변환
        }

        return result;
    }

    @Transactional
    public void deleteCardMe(int userId, int cardId) {
        existsById(cardId);

        int updateCount = cardMapper.softDeleteCard(userId, cardId);
        if (updateCount != 1) {
            log.error("내 카드 삭제 실패 - userId: {}, cardId: {}", userId, cardId);
           throw new MyCardDeletionFailedException();
        }
    }
    
}
