package com.payper.domain.card;

import com.payper.domain.card.dto.CardResponse;
import com.payper.domain.card.dto.RegisterCardMeRequest;
import com.payper.domain.card.exception.CardNotFoundException;
import com.payper.domain.card.exception.MyCardDeletionFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log4j2
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
    public void deleteCardMe(int userId, int cardId) {
        existsById(cardId);

        int updateCount = cardMapper.softDeleteCard(userId, cardId);
        if (updateCount != 1) {
            log.error("내 카드 삭제 실패 - userId: {}, cardId: {}", userId, cardId);
           throw new MyCardDeletionFailedException();
        }
    }
    
}
