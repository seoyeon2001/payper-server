//package com.payper.external.codef;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.payper.domain.card.mapper.CardMapper;
//import com.payper.domain.card.exception.DuplicateUserCardException;
//import com.payper.domain.user.UserService;
//import com.payper.external.codef.dto.CardRegistrationResult;
//import com.payper.external.codef.dto.FilteredCardByCompanyName;
//import com.payper.external.codef.dto.RegistrationStatus;
//import com.payper.external.codef.dto.output.CardInfo;
//import com.payper.external.codef.dto.output.CodefStandardResponse;
//import com.payper.external.codef.dto.output.Result;
//import com.payper.external.codef.dto.request.ApprovalListRequest;
//import com.payper.external.codef.dto.request.ConnectedIdRequest;
//import com.payper.external.codef.dto.request.MyCardListRequest;
//import com.payper.external.codef.dto.response.ApprovalListResponse;
//import com.payper.external.codef.dto.response.CardSimilarityResponse;
//import com.payper.external.codef.dto.response.ConnectedIdResponse;
//import com.payper.external.codef.dto.response.MyCardListResponse;
//import com.payper.external.codef.exception.AlreadyLinkedCodefAccountException;
//import com.payper.external.codef.exception.CodefAccountNotLinkedException;
//import com.payper.external.codef.exception.CodefResponseFailureException;
//import com.payper.external.codef.exception.EncryptPasswordFailedException;
//import com.payper.external.codef.exception.MismatchedConnectedIdException;
//import com.payper.external.codef.exception.MissingConnectedIdInCodefResponseException;
//import com.payper.external.codef.util.CardSimilarityService;
//import io.codef.api.EasyCodef;
//import io.codef.api.EasyCodefServiceType;
//import io.codef.api.EasyCodefUtil;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class CodefService2 {
//    private final EasyCodef codef;
//    private final UserService userService;
//    private final ObjectMapper objectMapper;
//    private final CodefMapper codefMapper;
//    private final CardSimilarityService cardSimilarityService;
//    private final CardMapper cardMapper;
//    private static final String MY_CARD_LIST_URL = "/v1/kr/card/p/account/card-list";
//
//
//    private void validateCodefResponse(Map<String, Object> responseMap, String connectedId) {
//        // Result 정보 추출 및 검증
//        Map<String, Object> resultMap = (Map<String, Object>) responseMap.get("result");
//        String resultCode = (String) resultMap.get("code");
//        checkCodefResultCode(resultCode);
//
//        // ConnectedId 검증
//        String dataConnectedId = (String) responseMap.get("connectedId");
//        if (!connectedId.equals(dataConnectedId)) {
//            throw new MismatchedConnectedIdException(connectedId);
//        }
//
//    }
//    public CodefStandardResponse<MyCardListResponse> getMyCardList(MyCardListRequest request, Integer userId) {
//        String connectedId = userService.getConnectedIdById(userId);
//
//        // 사용자는 존재하는데, connected id가 없는 경우
//        if (connectedId == null) {
//            throw new CodefAccountNotLinkedException();
//        }
//
//        HashMap<String, Object> parameterMap = new HashMap<>();
//        parameterMap.put("organization", request.organizationName().getCode());
//        parameterMap.put("connectedId", connectedId);
//        parameterMap.put("inquiryType", request.inquiryType());
//
//        try {
//            String resultJson = codef.requestProduct(MY_CARD_LIST_URL, EasyCodefServiceType.DEMO, parameterMap);
//            Map<String, Object> responseMap = objectMapper.readValue(resultJson, new TypeReference<>() {});
//
//            // 검증 로직 (Result, ConnectedId)
//            validateCodefResponse(responseMap, connectedId);
//
//            // 카드 데이터 변환
//            Object dataRaw = responseMap.get("data");
//            List<CardInfo> cardList = processCardResponse(dataRaw);
//
//            // TODO: 카드별 등록 처리 및 결과 수집
//
//            // >> codef로 받은 카드의 companyName
//            String companyName = request.organizationName().name();
//
//            List<CardRegistrationResult> registrationResults = processCardRegistrations(cardList, companyName, userId);
//
//            // 요약 메시지 생성
//            String summary = createSummaryMessage(registrationResults);
//
//            MyCardListResponse response = new MyCardListResponse(cardList, registrationResults, summary);
//            Result result = createResult((Map<String, Object>) responseMap.get("result"));
//
//
////            // TODO: 사용자별 카드 등록
////            for (CardInfo apiCard : cardList) {
//////                System.out.println("================== apiCard = " + apiCard);
////                String apiCardName = apiCard.getResCardName();
//////                System.out.println("================== apiCardName = " + apiCardName);
////
////                getCardId(apiCardName, companyName, userId);
////            }
////
////            MyCardListResponse response = new MyCardListResponse(cardList);
////
////            Result result = new Result (
////                    (String) resultMap.get("code"),
////                    (String) resultMap.get("extraMessage"),
////                    (String) resultMap.get("message"),
////                    (String) resultMap.get("transactionId")
////            );
//
//            return new CodefStandardResponse<>(result, response);
//
//        } catch (Exception e) {
//            throw new RuntimeException("응답 과정 중 오류가 발생했습니다.", e);
//        }
//    }
//
//    // 카드 등록 일괄 처리
//    private List<CardRegistrationResult> processCardRegistrations(List<CardInfo> cardList, String companyName, Integer userId) {
//        List<CardRegistrationResult> results = new ArrayList<>();
//
//        for (CardInfo apiCard : cardList) {
//            CardRegistrationResult result = registerUserCardWithResult(apiCard.getResCardName(), companyName, userId);
//            results.add(result);
//
//            // 서버 로그
//            log.info("카드 등록 처리: {} - {}", result.getApiCardName(), result.getStatus().getDescription());
//        }
//
//        return results;
//    }
//
//    // 개별 카드 등록 (결과 반환)
//    private CardRegistrationResult registerUserCardWithResult(String apiCardName, String companyName, Integer userId) {
//        try {
//            // 1. 카드사 기반 필터링
//            List<FilteredCardByCompanyName> dbCards = codefMapper.findCardByCompanyName(companyName);
//            if (dbCards.isEmpty()) {
//                return new CardRegistrationResult(apiCardName, null, null,
//                        RegistrationStatus.NO_MATCH,
//                        "해당 카드사의 카드가 없습니다.");
//            }
//
//            // 2. 유사도 매칭
//            List<String> dbCardNames = dbCards.stream()
//                    .map(FilteredCardByCompanyName::getName)
//                    .toList();
//
//            List<CardSimilarityResponse> recommendations =
//                    cardSimilarityService.recommendSimilarCards(apiCardName, 1, dbCardNames);
//
//            if (recommendations.isEmpty()) {
//                return new CardRegistrationResult(apiCardName, null, null,
//                        RegistrationStatus.NO_MATCH,
//                        "매칭되는 카드를 찾을 수 없습니다.");
//            }
//
//            String matchedCardName = recommendations.get(0).cardName();
//            Integer cardId = cardMapper.getId(matchedCardName);
//
//            if (cardId == null) {
//                return new CardRegistrationResult(apiCardName, matchedCardName, null,
//                        RegistrationStatus.FAILED,
//                        "카드 정보 오류");
//            }
//
//            // 3. 등록 상태 확인 및 처리
//            boolean isAlreadyActive = cardMapper.existsUserCard(userId, cardId);
//
//            if (isAlreadyActive) {
//                return new CardRegistrationResult(apiCardName, matchedCardName, cardId,
//                        RegistrationStatus.ALREADY_EXISTS,
//                        "이미 등록된 카드입니다.");
//            }
//
//            if (cardMapper.isPreviouslyDeletedUserCard(userId, cardId)) {
//                cardMapper.restoreUserCard(userId, cardId);
//                return new CardRegistrationResult(apiCardName, matchedCardName, cardId,
//                        RegistrationStatus.RESTORED,
//                        "삭제된 카드를 복원했습니다.");
//            } else {
//                cardMapper.registerMy(userId, cardId);
//                return new CardRegistrationResult(apiCardName, matchedCardName, cardId,
//                        RegistrationStatus.SUCCESS,
//                        "카드가 등록되었습니다.");
//            }
//
//        } catch (Exception e) {
//            log.error("카드 등록 처리 중 오류: apiCardName={}, userId={}", apiCardName, userId, e);
//            return new CardRegistrationResult(apiCardName, null, null,
//                    RegistrationStatus.FAILED,
//                    "등록 중 오류가 발생했습니다.");
//        }
//    }
//
//    // 요약 메시지 생성
//    private String createSummaryMessage(List<CardRegistrationResult> results) {
//        long successCount = results.stream().filter(r -> r.getStatus() == RegistrationStatus.SUCCESS).count();
//        long restoredCount = results.stream().filter(r -> r.getStatus() == RegistrationStatus.RESTORED).count();
//        long alreadyExistsCount = results.stream().filter(r -> r.getStatus() == RegistrationStatus.ALREADY_EXISTS).count();
//        long failedCount = results.stream().filter(r -> r.getStatus() == RegistrationStatus.NO_MATCH ||
//                r.getStatus() == RegistrationStatus.FAILED).count();
//
//        List<String> messages = new ArrayList<>();
//        if (successCount > 0) messages.add(successCount + "개 카드 신규 등록");
//        if (restoredCount > 0) messages.add(restoredCount + "개 카드 복원");
//        if (alreadyExistsCount > 0) messages.add(alreadyExistsCount + "개 카드 이미 등록됨");
//        if (failedCount > 0) messages.add(failedCount + "개 카드 등록 실패");
//
//        return String.join(", ", messages);
//    }
//
//
//    // 보유 카드 리스트
//    private List<CardInfo> processCardResponse(Object dataRaw) {
//        if (dataRaw == null) {
//            return Collections.emptyList();
//        }
//
//        try {
//            if (dataRaw instanceof List) {
//                // 다건 응답: data가 배열 [{}, {}]
//                return objectMapper.convertValue(dataRaw, new TypeReference<List<CardInfo>>() {});
//            } else {
//                // 단건 응답: data가 단일 객체 {}
//                CardInfo card = objectMapper.convertValue(dataRaw, CardInfo.class);
//                return List.of(card);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("카드 정보 변환 실패", e);
//        }
//    }
//
//    private void checkCodefResultCode(String resultCode) {
//        if (!"CF-00000".equals(resultCode)) {
//            throw new CodefResponseFailureException(resultCode);
//        }
//    }
//
//}
