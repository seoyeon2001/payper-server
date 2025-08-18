package com.payper.global.notification.service;

import com.google.firebase.messaging.Notification;
import com.payper.domain.card.mapper.CardMapper;
import com.payper.domain.card.dto.response.CardResponse;
import com.payper.domain.partner.PartnerService;
import com.payper.domain.partner.dto.PartnerKeywordSearchRequest;
import com.payper.domain.partner.dto.PartnerKeywordSearchResponse;
import com.payper.domain.partner.dto.PartnerTempDto;
import com.payper.global.notification.dto.FcmPartnerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final PartnerService partnerService;
    private final CardMapper cardMapper;

    public Optional<Notification> buildPartnerNotification(Integer userId, PartnerKeywordSearchRequest request) {
        FcmPartnerResponse fcmPartnerResponse = findPartnerAndCard(request, userId);

        if(fcmPartnerResponse == null) {
            return Optional.empty();
        }

        // 위치 기반으로 메시지 생성
        String title = String.format("📢%s에서 혜택을 받으실 수 있어요!", fcmPartnerResponse.getPartnerName());
        String body = String.format("💳%s로 \'%s\' 혜택을 즐겨보세요.",
                fcmPartnerResponse.getCardName(),
                fcmPartnerResponse.getCardSummary());

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        return Optional.of(notification);
    }

    private FcmPartnerResponse findPartnerAndCard(
            PartnerKeywordSearchRequest request,
            Integer userId
    ) {
        String keyword = request.getQuery();

        // 카카오맵 api에서 가맹점 리스트 조회
        PartnerKeywordSearchResponse response = partnerService.findNearbyKeyword(request);

        // DB에서 keyword로 가맹점 후보 조회
        List<PartnerTempDto> matchedPartners = partnerService.findPartnersByQuery(keyword);

        // Response 값 채우기
        for (PartnerKeywordSearchResponse.Document document : response.getDocuments()) {
            String docCategoryName = document.getCategoryName();

            // category_name에 keyword가 포함되지 않으면 건너뜀
            if (document == null ||
                    docCategoryName == null ||
                    !docCategoryName.toLowerCase().contains(keyword.toLowerCase())) {
                continue;
            }

            PartnerTempDto matchedPartner = partnerService.matchPartnerFromPlaceName(document.getPlaceName(), matchedPartners);
            if (matchedPartner == null) continue;

            CardResponse cardResponse = cardMapper.selectOneByPartnerId(userId, matchedPartner.getPartnerId());
            if (cardResponse == null || cardResponse.getBenefits() == null || cardResponse.getBenefits().isEmpty()) {
                continue;
            }

            return FcmPartnerResponse.builder()
                    .partnerName(document.getPlaceName())
                    .cardName(cardResponse.getName())
                    .cardSummary(cardResponse.getBenefits().get(0).getSummary())
                    .build();
        }

        log.error("사용자 ID {}가 해당 위치에서 혜택 받을 수 있는 지점이 없습니다.", userId);
        return null;
    }

}
