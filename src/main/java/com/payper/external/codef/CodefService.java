package com.payper.external.codef;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payper.external.codef.dto.output.CardInfo;
import com.payper.external.codef.dto.output.CodefStandardResponse;
import com.payper.external.codef.dto.output.Result;
import com.payper.external.codef.dto.request.ConnectedIdRequest;
import com.payper.external.codef.dto.request.MyCardListRequest;
import com.payper.external.codef.dto.response.ConnectedIdResponse;
import com.payper.external.codef.dto.response.MyCardListResponse;
import com.payper.domain.user.UserService;
import com.payper.external.codef.exception.*;
import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefServiceType;
import io.codef.api.EasyCodefUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            accountMap.put("organization", request.organizationCode().getCode());
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
        parameterMap.put("organization", request.organizationCode().getCode());
        parameterMap.put("connectedId", connectedId);
        parameterMap.put("inquiryType", request.inquiryType());

        try {
            String myCardListURL = "/v1/kr/card/p/account/card-list";
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

            // 사용자별 카드 등록


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

    private void checkCodefResultCode(String resultCode) {
        if (!"CF-00000".equals(resultCode)) {
            throw new CodefResponseFailureException(resultCode);
        }
    }
}
