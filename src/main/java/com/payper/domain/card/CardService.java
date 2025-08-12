package com.payper.domain.card;

import com.payper.domain.benefit.exception.BenefitDeletionFailedException;
import com.payper.domain.card.dto.CardResponse;
import com.payper.domain.card.dto.RegisterCardMeRequest;
import com.payper.domain.card.dto.RegisterCardRequest;
import com.payper.domain.card.dto.UpdateCardRequest;
import com.payper.domain.card.exception.*;
import com.payper.domain.user.exception.UserCardDeletionFailedException;
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
        return cardMapper.selectAll();
    }

    public CardResponse getCardById(Integer cardId) {
        existsByCardId(cardId);
        return cardMapper.selectById(cardId);
    }


    // 존재하고 삭제되지 않은 카드인지 확인하기 위함 - 단순 검증용이므로 void
    private void existsByCardId(Integer cardId) {
        if(!cardMapper.existsById(cardId)) {
            throw new CardNotFoundException();
        }
    }

    public List<CardResponse> searchCards(String name, String type, List<String> category, List<String> cardCompany) {
        if (name == null && type == null && category == null && cardCompany == null) {
            // 전체 카드 조회
            return cardMapper.selectAll();
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
            cardMapper.registerMy(userId, request.getCardId());
        }
    }

    // 이미 등록된 카드인지 확인하기 위함 - 단순 검증용이므로 void
    private void checkDuplicateUserCard(Integer userId, Integer cardId) {
        if (cardMapper.existsUserCard(userId, cardId)) {
            throw new DuplicateUserCardException(userId, cardId);
        }
    }

    private void existsByCardCompanyName(String cardCompanyName) {
        if(!cardMapper.existsCardCompany(cardCompanyName)){
            throw new CardCompanyNotFoundException();
        }
    }

    @Transactional
    public void registerCard(RegisterCardRequest request){
        String cardCompanyName=request.getCompanyName();

        existsByCardCompanyName(cardCompanyName);

        Integer cardCompanyId=cardMapper.getCompanyId(cardCompanyName);

        Integer result=cardMapper.register(request,cardCompanyId);

        if(result!=1){
            throw new CardRegisterationFailedException();
        }
    }

    // 등록되었는지 확인. 삭제여부 확인X.
    private void existedByCardId(Integer cardId){
        if(!cardMapper.existedById(cardId)){
            throw new CardExistedNotFoundException();
        }
    }

    @Transactional
    public void updateCard(UpdateCardRequest request, Integer cardId){
        String cardCompanyName=request.getCompanyName();

        existsByCardCompanyName(cardCompanyName);

        existsByCardId(cardId);

        Integer cardCompanyId=cardMapper.getCompanyId(cardCompanyName);

        Integer result=cardMapper.update(request,cardCompanyId,cardId);

        if(result!=1){
            throw new CardUpdateFailedException(cardId);
        }
    }

    @Transactional
    public void deleteCardMe(Integer userId, Integer cardId) {
        existedByCardId(cardId);

        // 삭제 여부 확인
        if (cardMapper.isPreviouslyDeletedUserCard(userId, cardId)) {
            throw new AlreadyDeletedUserCardException();
        }

        Integer updateCount = cardMapper.deleteCardMe(userId, cardId);
        if (updateCount != 1) {
            throw new MyCardDeletionFailedException();
        }
    }

    @Transactional
    public void deactivateCard(Integer cardId) {
        existsByCardId(cardId);

        Integer result= cardMapper.softDeactivateCard(cardId);

        if(result!=1){
            throw new CardDeletionFailedException();
        }
    }

    @Transactional
    public void deleteCard(Integer cardId) {
        existsByCardId(cardId);

        Integer result= cardMapper.softDelete(cardId);

        if(result!=1){
            throw new CardDeletionFailedException();
        }

        result = cardMapper.softDeleteUserCard(cardId);

        if(result!=1){
            throw new UserCardDeletionFailedException();
        }

        result = cardMapper.softDeleteBenefit(cardId);

        if(result!=1){
            throw new BenefitDeletionFailedException(cardId, null);
        }
    }
}
