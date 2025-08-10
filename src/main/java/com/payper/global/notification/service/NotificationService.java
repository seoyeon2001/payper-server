package com.payper.global.notification.service;

import com.payper.domain.card.CardMapper;
import com.payper.domain.card.dto.CardResponse;
import com.payper.domain.partner.PartnerService;
import com.payper.domain.partner.dto.PartnerKeywordSearchRequest;
import com.payper.domain.partner.dto.PartnerKeywordSearchResponse;
import com.payper.domain.partner.dto.PartnerTempDto;
import com.payper.global.notification.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final FcmService fcmService;
    private final PartnerService partnerService;
    private final CardMapper cardMapper;

    public boolean sendToUser(Integer userId, PartnerKeywordSearchRequest request) {
        NotificationResponse notificationResponse = findPartnerAndCard(request, userId);

        if(notificationResponse == null) {
            log.error("사용자 ID {}가 해당 위치에서 혜택 받을 수 있는 지점이 없습니다.", userId);
            return false;
        }

        // 위치 기반으로 메시지 생성
        String title = String.format("📢%s에서 혜택을 받으실 수 있어요!", notificationResponse.getPartnerName());
        String body = String.format("💳%s로 \'%s\' 혜택을 즐겨보세요.",
                notificationResponse.getCardName(),
                notificationResponse.getCardSummary());

        System.out.println("title = " + title);
        System.out.println("body = " + body);

        fcmService.send(title, body);
        return true;
    }



    private NotificationResponse findPartnerAndCard(
            PartnerKeywordSearchRequest request,
            Integer userId
    ) {
        String keyword = request.getQuery();

        // 카카오맵 api에서 가맹점 리스트 조회
        PartnerKeywordSearchResponse response = partnerService.findNearbyKeyword(request);

        // DB에서 keyword로 가맹점 후보 조회
        List<PartnerTempDto> matchedPartners = partnerService.findPartnersByQuery(keyword);

        // Response 값 채우기
        List<PartnerKeywordSearchResponse.Document> documents = response.getDocuments();
        for (PartnerKeywordSearchResponse.Document document : documents) {
            if (document == null) {
                // 문서가 null이면 건너뜀
                log.warn("null 문서가 발견되었습니다.");
                continue;
            }

            String docCategoryName = document.getCategoryName();

            // category_name에 keyword가 포함되지 않으면 건너뜀
            if (docCategoryName == null ||
                    !docCategoryName.toLowerCase().contains(keyword.toLowerCase())) {
                continue;
            }

            PartnerTempDto matchedPartner = partnerService.matchPartnerFromPlaceName(document.getPlaceName(), matchedPartners);
            if (matchedPartner == null) continue;

            Integer partnerId = matchedPartner.getPartnerId();

            CardResponse cardResponse = cardMapper.selectOneByPartnerId(userId, partnerId);
            if (cardResponse == null) continue;

            String cardName = cardResponse.getName();
            String cardBenefit = cardResponse.getBenefits().get(0).getSummary();

            return NotificationResponse.builder()
                    .partnerName(document.getPlaceName())
                    .cardName(cardName)
                    .cardSummary(cardBenefit)
                    .build();
        }
        log.warn("문서가 존재하지 않습니다.");
        return null;
    }

}
