package com.payper.domain.user;

import com.payper.domain.card.CardMapper;
import com.payper.domain.partner.PartnerMapper;
import com.payper.domain.user.domain.UserCardTransaction;
import com.payper.domain.user.exception.UserCardTransactionSaveFailedException;
import com.payper.external.codef.dto.output.ApprovalInfo;
import com.payper.external.crawling.config.partnerSynonyms;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCardTransactionService {
    private final UserCardMapper userCardMapper;
    private final CardMapper cardMapper;
    private final PartnerMapper partnerMapper;
    private final UserCardTransactionMapper userCardTransactionMapper;

    @Transactional
    public void saveApprovalTransactions(List<ApprovalInfo> approvalList, Integer userId) {
        int savedCount = 0;
        int skippedCount = 0;

        for (ApprovalInfo approval : approvalList) {
            log.info(String.valueOf(approval));

            try {
                // codef를 통해 받아온 cardName의 cardId 값 찾기
                String codefCardName = approval.getResCardName();
                Integer cardId = cardMapper.getCardId(codefCardName);
//                log.info("codef를 통해 받아온 cardName: {} 을 통해 cardId를 추출합니다. {}", codefCardName, cardId);

                if (cardId == null) {
                    log.warn("Card not found for cardName: {}", codefCardName);
                    skippedCount++;
                    continue;
                }

                // cardId를 구해서 userCardId 추출
                Integer userCardId = userCardMapper.getByUserIdAndCardId(userId, cardId).getUserCardId();
//                log.info("cardId와 userId를 통해 userCardId를 추출합니다. {}", userCardId);

                if (userCardId == null) {
                    log.warn("UserCard not found for userId: {}, cardId: {}", userId, cardId);
                    skippedCount++;
                    continue;
                }

//                // partnerId 찾기
//                String memberStoreName = approval.getResMemberStoreName();
//                Integer partnerId = getPartnerIdFromDatabase(memberStoreName);
//                log.info("결제 가맹점: {} 의 결과를 통해 partnerId를 추출합니다. {}", memberStoreName, partnerId);
//
//                // partnerId가 null이어도 거래는 저장할 수 있도록 처리
//                if (partnerId == null) {
//                    partnerId = 0; // 없는 가맹점의 기본값, DB에 0번 파트너 생성 가능
//                    log.warn("Partner not found for memberStoreName: {}", memberStoreName);
//                }


                /*
                // 4. 중복 거래 확인 (선택사항)
                boolean exists = userCardTransactionMapper.existsByApprovalInfo(
                        approval.getResApprovalNo(),
                        approval.getResUsedDate(),
                        approval.getResUsedTime(),
                        userCardId
                );

                if (exists) {
                    log.debug("Transaction already exists - approval: {}, date: {}, time: {}",
                            approval.getResApprovalNo(), approval.getResUsedDate(), approval.getResUsedTime());
                    skippedCount++;
                    continue;
                }

                 */

                // user_card_transaction 테이블에 저장
                UserCardTransaction transaction = approval.toDomain(userCardId, 1);
//                UserCardTransaction transaction = approval.toDomain(userCardId, partnerId);
//                UserCardTransaction transaction = createUserCardTransaction(approval, userCardId, partnerId);


                if (userCardTransactionMapper.save(transaction) == 1) {
                    savedCount++;
                    log.debug("Successfully saved transaction for approval: {}", approval.getResApprovalNo());
                } else {
                    log.error("Failed to save transaction for approval: {}", approval.getResApprovalNo());
                    skippedCount++;
                }

            } catch (Exception e) {
                log.error("Failed to process approval: {}", approval.getResApprovalNo(), e);
                skippedCount++;
            }
            log.info("Transaction save completed - saved: {}, skipped: {}", savedCount, skippedCount);
        }
    }

    /**
     * memberStoreName에서 파트너명을 추출하고 해당 파트너의 ID를 반환
     * @param memberStoreName 예: "투썸플레이스 청량리점"
     * @return 파트너 ID (찾지 못한 경우 null)
     */
    public Integer getPartnerIdByPartnerName(String memberStoreName) {
        if (memberStoreName == null || memberStoreName.trim().isEmpty()) {
            return null;
        }

        // 1. 파트너명 추출
        String partnerName = extractPartnerName(memberStoreName);
        if (partnerName == null) {
            log.debug("No partner name found in memberStoreName: {}", memberStoreName);
            return null;
        }

        // 2. 데이터베이스에서 파트너 ID 조회
        Integer partnerId = getPartnerIdFromDatabase(partnerName);

        if (partnerId != null) {
            log.debug("Found partnerId: {} for partnerName: {} from memberStoreName: {}",
                    partnerId, partnerName, memberStoreName);
        } else {
            log.debug("No partnerId found for partnerName: {} from memberStoreName: {}",
                    partnerName, memberStoreName);
        }

        return partnerId;
    }

    /**
     * 파트너명으로 데이터베이스에서 파트너 ID를 조회
     * codef를 통해 받아온 partnerName을 통해 partnerId를 가져옴
     * @param partnerName 파트너명
     * @return 파트너 ID
     */
    private Integer getPartnerIdFromDatabase(String partnerName) {
        try {
            return partnerMapper.findIdByName(partnerName);
        } catch (Exception e) {
            log.error("Failed to get partnerId for partnerName: {}", partnerName, e);
            return null;
        }
    }

    /**
     * 금액 문자열을 Integer로 파싱
     */
    private Integer parseAmount(String amount) {
        if (amount == null || amount.trim().isEmpty()) {
            return null;
        }
        try {
            // 쉼표나 다른 문자 제거 후 파싱
            String cleanAmount = amount.replaceAll("[^0-9-]", "");
            return Integer.parseInt(cleanAmount);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse amount: {}", amount);
            return null;
        }
    }

    /**
     * memberStoreName에서 파트너명을 추출
     * @param memberStoreName 예: "투썸플레이스 청량리점"
     * @return 추출된 파트너명 (예: "투썸플레이스")
     */
    public String extractPartnerName(String memberStoreName) {
        if (memberStoreName == null || memberStoreName.trim().isEmpty()) {
            return null;
        }

        String normalizedStoreName = memberStoreName.toLowerCase().trim();

        // partnerSynonyms의 모든 파트너를 순회
        for (Map.Entry<String, List<String>> entry : partnerSynonyms.ps.entrySet()) {
            String partnerName = entry.getKey();
            List<String> synonyms = entry.getValue();

            // 1. 정확한 파트너명이 포함되어 있는지 확인
            if (memberStoreName.contains(partnerName)) {
                return partnerName;
            }

            // 2. 동의어들이 포함되어 있는지 확인
            for (String synonym : synonyms) {
                if (normalizedStoreName.contains(synonym.toLowerCase())) {
                    return partnerName;
                }
            }
        }

        return null;
    }

    //승인내역 한 개 저장
    @Transactional
    public void save(Integer userId,Integer cardId, ApprovalInfo  approvalInfo) {
        try {
            // 1. userCardId 추출
            Integer userCardId = userCardMapper.getByUserIdAndCardId(userId, cardId).getUserCardId();

            if (userCardId == null) {
                throw new UserCardTransactionSaveFailedException();
            }

            // 2. partnerId 찾기
            String memberStoreName = approvalInfo.getResMemberStoreName();
            Integer partnerId = getPartnerIdByPartnerName(memberStoreName);

            // 3. UserCardTransaction 생성 및 저장
            UserCardTransaction userCardTransaction = approvalInfo.toDomain(userCardId, partnerId);

            if (userCardTransactionMapper.save(userCardTransaction) != 1) {
                throw new UserCardTransactionSaveFailedException();
            }

            log.info("Successfully saved single transaction for approval: {}", approvalInfo.getResApprovalNo());

        } catch (Exception e) {
            log.error("Failed to save single approval transaction", e);
            throw new UserCardTransactionSaveFailedException();
        }

    }
}
