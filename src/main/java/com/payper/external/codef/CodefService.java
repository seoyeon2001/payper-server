package com.payper.external.codef;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payper.domain.card.CardMapper;
import com.payper.external.codef.dto.CardRegistrationResult;
import com.payper.external.codef.dto.RegistrationStatus;
import com.payper.external.codef.dto.output.CardInfo;
import com.payper.external.codef.dto.output.CodefStandardResponse;
import com.payper.external.codef.dto.output.Result;
import com.payper.external.codef.dto.request.ApprovalListRequest;
import com.payper.external.codef.dto.request.ConnectedIdRequest;
import com.payper.external.codef.dto.request.MyCardListRequest;
import com.payper.external.codef.dto.response.ApprovalListResponse;
import com.payper.external.codef.dto.response.ConnectedIdResponse;
import com.payper.external.codef.dto.response.MyCardListResponse;
import com.payper.domain.user.UserService;
import com.payper.external.codef.exception.*;
import com.payper.external.codef.dto.FilteredCardByCompanyName;
import com.payper.external.codef.dto.response.CardSimilarityResponse;
import com.payper.external.codef.util.CardSimilarityService;
import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefServiceType;
import io.codef.api.EasyCodefUtil;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodefService {
    private final EasyCodef codef;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final CodefMapper codefMapper;
    private final CardSimilarityService cardSimilarityService;
    private final CardMapper cardMapper;
    private static final String MY_CARD_LIST_URL = "/v1/kr/card/p/account/card-list";
    private static final String APPROVAL_LIST_URL = "/v1/kr/card/p/account/approval-list";

    public CodefStandardResponse<ConnectedIdResponse> createConnectedId(ConnectedIdRequest request, Integer userId) {
        String connectedId = userService.getConnectedIdById(userId);

        // connected id가 있는 사용자
        if (connectedId != null) {
            throw new AlreadyLinkedCodefAccountException();
        }

        try {
            // 1. CODEF 요청 파라미터 구성
            HashMap<String, Object> accountMap = buildAccountMap(request);
            List<HashMap<String, Object>> accountList = List.of(accountMap);
            HashMap<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("accountList", accountList);

            // 2. CODEF API 호출 및 응답 파싱
            String resultJson = codef.createAccount(EasyCodefServiceType.DEMO, parameterMap);
            HashMap<String, Object> responseMap = objectMapper.readValue(resultJson, HashMap.class);

            HashMap<String, Object> resultMap = (HashMap<String, Object>) responseMap.get("result");
            HashMap<String, Object> dataMap = (HashMap<String, Object>) responseMap.get("data");

            String resultCode = (String) resultMap.get("code");
            checkCodefResultCode(resultCode);

            // 3. 응답 객체 구성 및 저장
            connectedId = (String) dataMap.get("connectedId");
            if (connectedId == null || connectedId.isBlank()) {
                throw new MissingConnectedIdInCodefResponseException();
            }

            userService.updateConnectedId(userId, connectedId);

            // Result 객체 생성
            Result result = createResult(resultMap);
            ConnectedIdResponse response = objectMapper.convertValue(dataMap, ConnectedIdResponse.class);
            return new CodefStandardResponse<>(result, response);

        } catch (Exception e) {
            throw new RuntimeException("ConnectedId 생성 중 오류가 발생했습니다.", e);
        }
    }

    private HashMap<String, Object> buildAccountMap(ConnectedIdRequest request) {
        try {
            HashMap<String, Object> accountMap = new HashMap<>();
            accountMap.put("countryCode", "KR"); // 한국
            accountMap.put("businessType", "CD"); // 카드
            accountMap.put("clientType", "P"); // 개인
            accountMap.put("organization", request.organizationName().getCode());
            accountMap.put("loginType", "1"); // 아이디 비번 로그인
            accountMap.put("id", request.companyId());
            accountMap.put("password", EasyCodefUtil.encryptRSA(request.companyPassword(), codef.getPublicKey()));
            return accountMap;
        } catch (Exception e) {
            throw new EncryptPasswordFailedException();
        }
    }

    public MyCardListResponse getMyCardList(MyCardListRequest request, Integer userId) {
        String connectedId = userService.getConnectedIdById(userId);

        // 사용자는 존재하는데, connected id가 없는 경우
        if (connectedId == null) {
            throw new CodefAccountNotLinkedException();
        }

        HashMap<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("organization", request.organizationName().getCode());
        parameterMap.put("connectedId", connectedId);
        parameterMap.put("inquiryType", request.inquiryType());

        try {
            String resultJson = codef.requestProduct(MY_CARD_LIST_URL, EasyCodefServiceType.DEMO, parameterMap);
            
            // Json -> java 객체
            Map<String, Object> responseMap = objectMapper.readValue(resultJson, new TypeReference<>() {});

            // 검증 로직 (result, connectedId)
            validateCodefResponse(responseMap, connectedId);

            // 카드 데이터 변환
            Object dataRaw = responseMap.get("data");
            List<CardInfo> apiCardList = processCardResponse(dataRaw);

            // codef로 받은 카드의 companyName
            String companyName = request.organizationName().name();

            // 카드사가 소유한 카드 리스트 조회 - 카드사이름 기반 DB 필터링
            List<FilteredCardByCompanyName> dbCardList = codefMapper.findCardByCompanyName(companyName);

            // 내 카드로 등록 처리
            List<CardRegistrationResult> registrationResults = processCardRegistrations(apiCardList, dbCardList, userId);

            // 요약 메시지 생성
            String summary = createSummaryMessage(registrationResults);

            MyCardListResponse response = new MyCardListResponse(apiCardList, registrationResults, summary);

            return response;

        } catch (Exception e) {
            throw new RuntimeException("응답 과정 중 오류가 발생했습니다.", e);
        }
    }

    public CodefStandardResponse<List<ApprovalListResponse>> getApprovalList(ApprovalListRequest request, Integer cardId, Integer userId) {
        String connectedId = userService.getConnectedIdById(userId);

        // 사용자는 존재하는데, connected id가 없는 경우
        if (connectedId == null) {
            throw new CodefAccountNotLinkedException();
        }

        HashMap<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("organization", request.organizationName().getCode());
        parameterMap.put("connectedId", connectedId);
        parameterMap.put("startDate", request.startDate());
        parameterMap.put("endDate", request.endDate());
        parameterMap.put("orderBy", request.orderBy());
        parameterMap.put("inquiryType", "1");
//        parameterMap.put("cardName", request.cardName());
//        parameterMap.put("duplicateCardIdx", request.duplicateCardIdx());
//        parameterMap.put("cardNo", request.cardNo());
//        parameterMap.put("cardPassword", request.cardPassword());
        parameterMap.put("memberStoreInfoType", request.memberStoreInfoType());

        try {
            String resultJson = codef.requestProduct(APPROVAL_LIST_URL, EasyCodefServiceType.DEMO, parameterMap);

            HashMap<String, Object> responseMap = objectMapper.readValue(resultJson, HashMap.class);
            HashMap<String, Object> resultMap = (HashMap<String, Object>)responseMap.get("result");
            Object dataRaw = responseMap.get("data");

            List<ApprovalListResponse> approvalList = new ArrayList<>();

            if (dataRaw instanceof List) {
                approvalList = objectMapper.convertValue(
                        dataRaw,
                        new TypeReference<>() {}
                );
            } else if (dataRaw instanceof Map) {
                ApprovalListResponse approval = objectMapper.convertValue(
                        dataRaw,
                        ApprovalListResponse.class
                );
                approvalList.add(approval);
            }

            String resultCode = (String) resultMap.get("code");
            checkCodefResultCode(resultCode);

            String dataConnectedId = (String) responseMap.get("connectedId");
            if (!connectedId.equals(dataConnectedId)) {
                throw new MismatchedConnectedIdException(connectedId);
            }

            // Result 객체 생성
            Result result = createResult(resultMap);
            return new CodefStandardResponse<>(result, approvalList);

        } catch (Exception e) {
            throw new RuntimeException("응답 과정 중 오류가 발생했습니다.", e);
        }
    }

    // 보유 카드 리스트
    private List<CardInfo> processCardResponse(Object dataRaw) {
        if (dataRaw == null) {
            return Collections.emptyList();
        }

        try {
            if (dataRaw instanceof List) {
                // 다건 응답: data가 배열 [{}, {}]
                return objectMapper.convertValue(dataRaw, new TypeReference<>() {});
            } else {
                // 단건 응답: data가 단일 객체 {}
                CardInfo card = objectMapper.convertValue(dataRaw, CardInfo.class);
                return List.of(card);
            }
        } catch (Exception e) {
            log.error("Failed to convert card data: {}", dataRaw, e);
            throw new RuntimeException("카드 정보 변환 실패", e);
        }
    }

    private void checkCodefResultCode(String resultCode) {
        if (!"CF-00000".equals(resultCode)) {
            throw new CodefResponseFailureException(resultCode);
        }
    }

    // Result 생성 헬퍼 메서드
    private Result createResult(Map<String, Object> resultMap) {
        return new Result(
                (String) resultMap.get("code"),
                (String) resultMap.get("extraMessage"),
                (String) resultMap.get("message"),
                (String) resultMap.get("transactionId")
        );
    }

    private void validateCodefResponse(Map<String, Object> responseMap, String connectedId) {
        // Result 정보 추출 및 검증
        Map<String, Object> resultMap = (Map<String, Object>) responseMap.get("result");
        String resultCode = (String) resultMap.get("code");
        checkCodefResultCode(resultCode);

        // ConnectedId 검증
        String dataConnectedId = (String) responseMap.get("connectedId");
        if (!connectedId.equals(dataConnectedId)) {
            throw new MismatchedConnectedIdException(connectedId);
        }

    }

    // 카드 등록 일괄 처리
    private List<CardRegistrationResult> processCardRegistrations(List<CardInfo> cardList, List<FilteredCardByCompanyName> dbCardList, Integer userId) {
        List<CardRegistrationResult> results = new ArrayList<>();

        if (dbCardList.isEmpty()) {
            for (CardInfo apiCard : cardList) {
                results.add(new CardRegistrationResult(
                        apiCard.getResCardName(), null, null,
                        RegistrationStatus.NO_MATCH,
                        "해당 카드사의 카드가 없습니다."));
            }
            return results;
        }

        // DB 카드 이름만 추출
        List<String> dbCardNames = dbCardList.stream()
                .map(FilteredCardByCompanyName::getName)
                .toList();

        for (CardInfo apiCard : cardList) {
            CardRegistrationResult result = registerUserCardWithResult(apiCard.getResCardName(), dbCardNames, userId);
            results.add(result);

            log.info("카드 등록 처리: {} - {}", result.getApiCardName(), result.getStatus().getDescription());
        }

        return results;
    }

    // 개별 카드 등록 (결과 반환)
    private CardRegistrationResult registerUserCardWithResult(String apiCardName, List<String> dbCardNames, Integer userId) {
        try {
            // 유사도 매칭 - 가장 유사한 1개 추출
            List<CardSimilarityResponse> recommendations =
                    cardSimilarityService.recommendSimilarCards(apiCardName, 2, dbCardNames);

            if (recommendations.isEmpty()) {
                return new CardRegistrationResult(apiCardName, null, null,
                        RegistrationStatus.NO_MATCH,
                        "매칭되는 카드를 찾을 수 없습니다.");
            }

            // 매칭된 카드 이름 및 cardId 추출
            String matchedCardName = recommendations.get(0).cardName();
            Integer cardId = cardMapper.getId(matchedCardName);

            if (cardId == null) {
                return new CardRegistrationResult(apiCardName, matchedCardName, null,
                        RegistrationStatus.FAILED,
                        "카드 정보 오류");
            }

            // 등록된 이력 상태 확인 및 처리
            boolean isAlreadyActive = cardMapper.existsUserCard(userId, cardId);

            if (isAlreadyActive) { // 이미 등록된 경우
                return new CardRegistrationResult(apiCardName, matchedCardName, cardId,
                        RegistrationStatus.ALREADY_EXISTS,
                        "이미 등록된 카드입니다.");
            }

            if (cardMapper.isPreviouslyDeletedUserCard(userId, cardId)) {
                cardMapper.restoreUserCard(userId, cardId);
                return new CardRegistrationResult(apiCardName, matchedCardName, cardId,
                        RegistrationStatus.RESTORED,
                        "삭제된 카드를 복원했습니다.");
            } else {
                cardMapper.registerMy(userId, cardId);
                return new CardRegistrationResult(apiCardName, matchedCardName, cardId,
                        RegistrationStatus.SUCCESS,
                        "카드가 등록되었습니다.");
            }

        } catch (Exception e) {
            log.error("카드 등록 처리 중 오류: apiCardName={}, userId={}", apiCardName, userId, e);
            return new CardRegistrationResult(apiCardName, null, null,
                    RegistrationStatus.FAILED,
                    "등록 중 오류가 발생했습니다.");
        }
    }

    // 요약 메시지 생성
    private String createSummaryMessage(List<CardRegistrationResult> results) {
        long successCount = results.stream().filter(r -> r.getStatus() == RegistrationStatus.SUCCESS).count();
        long restoredCount = results.stream().filter(r -> r.getStatus() == RegistrationStatus.RESTORED).count();
        long alreadyExistsCount = results.stream().filter(r -> r.getStatus() == RegistrationStatus.ALREADY_EXISTS).count();
        long failedCount = results.stream().filter(r -> r.getStatus() == RegistrationStatus.NO_MATCH ||
                r.getStatus() == RegistrationStatus.FAILED).count();

        List<String> messages = new ArrayList<>();
        if (successCount > 0) messages.add(successCount + "개 카드 신규 등록");
        if (restoredCount > 0) messages.add(restoredCount + "개 카드 복원");
        if (alreadyExistsCount > 0) messages.add(alreadyExistsCount + "개 카드 이미 등록됨");
        if (failedCount > 0) messages.add(failedCount + "개 카드 등록 실패");

        return String.join(", ", messages);
    }
}
