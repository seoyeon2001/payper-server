package com.payper.domain.user;

import com.payper.domain.card.CardMapper;
import com.payper.domain.partner.PartnerMapper;
import com.payper.domain.user.domain.UserCard;
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
    public void saveApprovalTransactions(List<ApprovalInfo> approvalList, Integer userId, List<UserCard> myCardList) {
        int savedCount = 0;
        int skippedCount = 0;

        for (ApprovalInfo approval : approvalList) {
            log.info(String.valueOf(approval));

            try {
                // user_card 테이블에 저장된 last3과 codef로부터 받아온 카드번호 비교
                String cardNo = approval.getResCardNo();
                String lastCardNo = cardNo.substring(cardNo.length() - 3);
                Integer userCardId = userCardMapper.getUserCardIdByLastCardNo(lastCardNo);
                log.info("해당 카드는 user_card 테이블의 {}번 카드 입니다.", userCardId);

                if (userCardId == null) {
//                    throw new UserCardTransactionSaveFailedException();
                    log.warn("UserCard not found for userId: {}, card name: {}", userId, approval.getResCardName());
                    skippedCount++;
                    continue;
                }

                // partnerId 찾기
                String memberStoreName = approval.getResMemberStoreName();
                Integer partnerId = getPartnerIdByPartnerName(memberStoreName);
                log.info("결제 가맹점: {} 의 결과를 통해 partnerId를 추출합니다. {}", memberStoreName, partnerId);

                // partnerId가 null이어도 거래는 저장할 수 있도록 처리
                if (partnerId == null) {
//                    partnerId = 0; // 없는 가맹점의 기본값, DB에 0번 파트너 생성 가능
                    log.info("Partner not found for memberStoreName: {}", memberStoreName);
                }

                // 중복 거래 확인
                boolean exists = userCardTransactionMapper.existsByApprovalInfo(
                        approval.getResApprovalNo(),
                        approval.getResUsedDate(),
                        approval.getResUsedTime(),
                        approval.getResUsedAmount(),
                        userCardId
                );

                if (exists) {
                    log.info("Transaction already exists - approval: {}, date: {}, time: {}",
                            approval.getResApprovalNo(), approval.getResUsedDate(), approval.getResUsedTime());
                    skippedCount++;
                    continue;
                }

                // user_card_transaction 테이블에 저장
                UserCardTransaction transaction = approval.toDomain(userCardId, partnerId);

                if (userCardTransactionMapper.save(transaction) == 1) {
                    savedCount++;
                    log.info("Successfully saved transaction for approval: {}", approval.getResApprovalNo());
                } else {
//                    throw new UserCardTransactionSaveFailedException();
                    log.error("Failed to save transaction for approval: {}", approval.getResApprovalNo());
                    skippedCount++;
                }

            } catch (Exception e) {
//                throw new UserCardTransactionSaveFailedException();
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

        // 파트너명 추출
        String partnerName = extractPartnerName(memberStoreName);
        log.info("======================== 추출한 파트너 이름입니다. {}", partnerName);
        if (partnerName == null) {
            log.info("No partner name found in memberStoreName: {}", memberStoreName);
            return null;
        }

        // 데이터베이스에서 파트너 ID 조회
        Integer partnerId = getPartnerIdFromDatabase(partnerName);

        if (partnerId != null) {
            log.info("Found partnerId: {} for partnerName: {} from memberStoreName: {}",
                    partnerId, partnerName, memberStoreName);
        } else {
            log.info("No partnerId found for partnerName: {} from memberStoreName: {}",
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
            String partnerName = entry.getKey().toLowerCase().trim();
            List<String> synonyms = entry.getValue();

            // 정확한 파트너명이 포함되어 있는지 확인
            if (memberStoreName.contains(partnerName)) {
                log.info("=====extractPartnerName의 정확한 파트너명 존재 확인: {}", memberStoreName);
                return entry.getKey();  // 원래 파트너명 (대소문자 유지) 반환
            }

            // 동의어들이 포함되어 있는지 확인
            for (String synonym : synonyms) {
                if (normalizedStoreName.contains(synonym.toLowerCase().trim())) {
                    log.info("=====extractPartnerName의 유사 파트너명 존재 확인: {}", memberStoreName);
                    return entry.getKey();  // 원래 파트너명 반환
                }
            }
        }

        return null;
    }
}
