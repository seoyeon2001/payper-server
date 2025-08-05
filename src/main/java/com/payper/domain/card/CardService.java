package com.payper.domain.card;

import com.payper.domain.card.dto.CardResponse;
import com.payper.domain.card.dto.RegisterCardMeRequest;
import com.payper.domain.card.dto.RegisterCardRequest;
import com.payper.domain.card.dto.UpdateCardRequest;
import com.payper.domain.card.exception.*;
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

    public CardResponse getCardById(Integer cardId) {
        existsByCardId(cardId);
        return cardMapper.selectCardById(cardId);
    }


    // 존재하고 삭제되지 않은 카드인지 확인하기 위함 - 단순 검증용이므로 void
    private void existsByCardId(Integer cardId) {
        if(!cardMapper.existsByCardId(cardId)) {
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
        existedByCardId(request.getCardId()); // 존재하는 card인지 확인
        checkDuplicateUserCard(userId, request.getCardId()); // 이미 사용자 카드로 등록되어 있는지 확인

        if(cardMapper.isPreviouslyDeletedUserCard(userId, request.getCardId())) { // 등록 이력이 있는지 확인
            cardMapper.restoreUserCard(userId, request.getCardId());
        } else {
            cardMapper.registerCardMe(userId, request.getCardId());
        }
    }

    // 이미 등록된 카드인지 확인하기 위함 - 단순 검증용이므로 void
    private void checkDuplicateUserCard(Integer userId, Integer cardId) {
        if (cardMapper.existsUserCard(userId, cardId)) {
            log.error("이미 등록된 카드 - userId: {}, cardId: {}", userId, cardId);
            throw new DuplicateUserCardException(userId, cardId);
        }
    }

    private void existsByCardCompanyName(String cardCompanyName) {
        if(!cardMapper.existsCardCompany(cardCompanyName)){
            log.error("카드사 Not Found - cardCompanyName: {}", cardCompanyName);

            throw new CardCompanyNotFoundException();
        }
    }

    @Transactional
    public void registerCard(RegisterCardRequest request){
        String cardCompanyName=request.getCompanyName();

        existsByCardCompanyName(cardCompanyName);

        Integer cardCompanyId=cardMapper.getCardCompanyId(cardCompanyName);

        Integer result=cardMapper.registerCard(request,cardCompanyId);

        if(result!=1){
            log.error("카드 등록 실패 - companyName: {}, cardName: {}", cardCompanyName, request.getCardName());

            throw new CardRegisterationFailedException();
        }
    }

    //등록되었는지 확인. 삭제여부 확인X.
    private void existedByCardId(Integer cardId){
        if(!cardMapper.existedByCardId(cardId)){
            log.error("Existed Card Not Found - cardId: {}", cardId);

            throw new CardExistedNotFoundException();
        }
    }

    @Transactional
    public void updateCard(UpdateCardRequest request, Integer cardId){
        String cardCompanyName=request.getCompanyName();

        existsByCardCompanyName(cardCompanyName);

        existsByCardId(cardId);

        Integer cardCompanyId=cardMapper.getCardCompanyId(cardCompanyName);

        Integer result=cardMapper.updateCard(request,cardCompanyId,cardId);

        if(result!=1){
            throw new CardUpdateFailedException(cardId);
        }
    }

    @Transactional
    public void deleteCardMe(Integer userId, Integer cardId) {
        existedByCardId(cardId);

        // 삭제 여부 확인
        if (cardMapper.isPreviouslyDeletedUserCard(userId, cardId)) {
            log.error("이미 삭제된 카드입니다 - userId: {}, cardId: {}", userId, cardId);
            throw new AlreadyDeletedUserCardException();
        }

        Integer updateCount = cardMapper.softDeleteMyCard(userId, cardId);
        if (updateCount != 1) {
            log.error("내 카드 삭제 실패 - userId: {}, cardId: {}", userId, cardId);
            throw new MyCardDeletionFailedException();
        }
    }

    @Transactional
    public void deleteCard(Integer cardId) {
        existsByCardId(cardId);

        Integer result= cardMapper.softDeleteCard(cardId);

        if(result!=1){

            log.error("카드 삭제 실패 -  cardId: {}", cardId);

            throw new CardDeletionFailedException();
        }
    }
}
