package com.payper.external.codef;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payper.domain.card.CardMapper;
import com.payper.domain.card.CardService;
import com.payper.domain.card.exception.DuplicateUserCardException;
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
import com.payper.global.security.domain.CustomUser;
import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefServiceType;
import io.codef.api.EasyCodefUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
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

            Result result = new Result (
                    (String) resultMap.get("code"),
                    (String) resultMap.get("extraMessage"),
                    (String) resultMap.get("message"),
                    (String) resultMap.get("transactionId")
            );

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

    public CodefStandardResponse<MyCardListResponse> getMyCardList(MyCardListRequest request, Integer userId) {
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
            String myCardListURL = MY_CARD_LIST_URL;
            String resultJson = codef.requestProduct(myCardListURL, EasyCodefServiceType.DEMO, parameterMap);

            HashMap<String, Object> responseMap = objectMapper.readValue(resultJson, HashMap.class);
            HashMap<String, Object> resultMap = (HashMap<String, Object>)responseMap.get("result");
            Object dataRaw = responseMap.get("data");

            List<CardInfo> cardList = new ArrayList<>();

            if (dataRaw instanceof List) {
                // 다건 응답
                cardList = objectMapper.convertValue(
                        dataRaw,
                        new TypeReference<List<CardInfo>>() {}
                );
            } else if (dataRaw instanceof Map) {
                // 단건 응답
                CardInfo card = objectMapper.convertValue(
                        dataRaw,
                        CardInfo.class
                );
                cardList.add(card);
            }

            MyCardListResponse response = new MyCardListResponse();
            response.setData(cardList);

            String resultCode = (String) resultMap.get("code");
            checkCodefResultCode(resultCode);

            String dataConnectedId = (String) responseMap.get("connectedId");
            if (!connectedId.equals(dataConnectedId)) {
                throw new MismatchedConnectedIdException(connectedId);
            }

            // codef로 받은 카드의 companyName
            String companyName = request.organizationName().name();

            // TODO: 사용자별 카드 등록
            for (CardInfo apiCard : cardList) {
//                System.out.println("================== apiCard = " + apiCard);
                String apiCardName = apiCard.getResCardName();
//                System.out.println("================== apiCardName = " + apiCardName);

                getCardId(apiCardName, companyName, userId);
            }

            Result result = new Result (
                    (String) resultMap.get("code"),
                    (String) resultMap.get("extraMessage"),
                    (String) resultMap.get("message"),
                    (String) resultMap.get("transactionId")
            );

            return new CodefStandardResponse<>(result, response);

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
            String approvalListURL = APPROVAL_LIST_URL;
            String resultJson = codef.requestProduct(approvalListURL, EasyCodefServiceType.DEMO, parameterMap);

            HashMap<String, Object> responseMap = objectMapper.readValue(resultJson, HashMap.class);
            HashMap<String, Object> resultMap = (HashMap<String, Object>)responseMap.get("result");
            Object dataRaw = responseMap.get("data");

            List<ApprovalListResponse> approvalList = new ArrayList<>();

            if (dataRaw instanceof List) {
                approvalList = objectMapper.convertValue(
                        dataRaw,
                        new TypeReference<List<ApprovalListResponse>>() {}
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

            Result result = new Result(
                    (String) resultMap.get("code"),
                    (String) resultMap.get("extraMessage"),
                    (String) resultMap.get("message"),
                    (String) resultMap.get("transactionId")
            );

            return new CodefStandardResponse<>(result, approvalList);


        } catch (Exception e) {
            throw new RuntimeException("응답 과정 중 오류가 발생했습니다.", e);
        }
    }



    private void getCardId(String apiCardName, String companyName, Integer userId) {
//        System.out.println("================== companyName = " + companyName);
        // 1. 카드사 기반으로 DB 카드 필터링
        List<FilteredCardByCompanyName> dbCards = codefMapper.findCardByCompanyName(companyName);
//        System.out.println("================== dbCards = " + dbCards);

        if (dbCards.isEmpty()) return;

        // 2. 유사도 계산을 위한 카드 이름 목록 준비
        List<String> dbCardNames = dbCards.stream()
                .map(FilteredCardByCompanyName::getName)
                .toList();

//        System.out.println("================== dbCardNames = " + dbCardNames);

        // 3. 가장 유사한 카드 1개 추천
        List<CardSimilarityResponse> recommendations =
                cardSimilarityService.recommendSimilarCards(apiCardName, 1, dbCardNames);

//        System.out.println("================== recommendations = " + recommendations);
        if (recommendations.isEmpty()) return;

        String matchedCardName = recommendations.get(0).cardName();

//        System.out.println("================== matchedCardName = " + matchedCardName);
        // 4. 매칭된 카드의 cardId 찾기
        Integer cardId = cardMapper.getId(matchedCardName);
//        System.out.println("================== cardId = " + cardId);

        if (cardId == null) return;

        boolean isAlreadyMyCard =  cardMapper.existsUserCard(userId, cardId);

        if (isAlreadyMyCard) {
            throw new DuplicateUserCardException(userId, cardId);
        }

        // 5. UserCard 등록
//        cardService.checkDuplicateUserCard(userId, cardId);

        if(cardMapper.isPreviouslyDeletedUserCard(userId, cardId)) { // 등록 이력이 있는지 확인
            cardMapper.restoreUserCard(userId, cardId);
        } else {
            cardMapper.registerMy(userId, cardId);
        }
    }

    private void checkCodefResultCode(String resultCode) {
        if (!"CF-00000".equals(resultCode)) {
            throw new CodefResponseFailureException(resultCode);
        }
    }

}
