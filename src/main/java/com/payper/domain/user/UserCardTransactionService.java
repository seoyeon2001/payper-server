package com.payper.domain.user;

import com.payper.domain.card.CardMapper;
import com.payper.domain.card.CardService;
import com.payper.domain.card.domain.Card;
import com.payper.domain.user.domain.UserCardTransaction;
import com.payper.domain.user.exception.UserCardTransactionSaveFailedException;
import com.payper.external.codef.dto.response.ApprovalListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCardTransactionService { //거의 codef service안에서 활용
    final private UserCardMapper userCardMapper;
    final private UserCardTransactionMapper userCardTransactionMapper;

    //승인내역 한 개 저장
    @Transactional
    public void save(Integer userId,Integer cardId,ApprovalListResponse approvalListResponse) {
        //approvalListResponse.getResMemberStoreName();// partnerId찾기
        //일단은 승인 내역을 저장할 때, partnerId 정보를 같이 저장하지 않게 했습니다.
        
        Integer userCardId=userCardMapper.getByUserIdAndCardId(userId,cardId).getUserCardId();

        UserCardTransaction userCardTransaction = approvalListResponse.toDomain(userCardId,null);

        if(userCardTransactionMapper.save(userCardTransaction)!=1){
            throw new UserCardTransactionSaveFailedException();
        }
    }
}
