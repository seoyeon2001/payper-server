package com.payper.card.service;

import com.payper.card.dto.CardResponse;
import com.payper.card.mapper.CardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

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
        return cardMapper.selectCardById(cardId);
    }

    public boolean existsById(int cardId) {
        return cardMapper.findById(cardId) == 1;
    }

    public List<CardResponse> searchCards(String name, String type, List<String> category, List<String> cardCompany) {
        if (name == null && type == null && category == null && cardCompany == null) {
            // 전체 카드 조회
            return cardMapper.selectAllCards();
        }

        // 파라미터에 맞는 조건으로 검색
        return cardMapper.searchWithConditions(name, type, category, cardCompany);
    }

    public List<CardResponse> getCardsByUserId(int userId){
        return cardMapper.selectCardsByUserID(userId);
    }

}
