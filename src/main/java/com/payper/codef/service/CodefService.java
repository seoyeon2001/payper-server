package com.payper.codef.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payper.codef.dto.output.CardInfo;
import com.payper.codef.dto.output.CodefStandardResponse;
import com.payper.codef.dto.output.Result;
import com.payper.codef.dto.request.ConnectedIdRequest;
import com.payper.codef.dto.request.MyCardListRequest;
import com.payper.codef.dto.response.ConnectedIdResponse;
import com.payper.codef.dto.response.MyCardListResponse;
import com.payper.user.domain.User;
import com.payper.user.service.UserService;
import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefServiceType;
import io.codef.api.EasyCodefUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CodefService {
    private final EasyCodef codef;
    private final UserService userService;

    public CodefStandardResponse<ConnectedIdResponse> createConnectedId(ConnectedIdRequest request, Integer userId) {
        User user = userService.getUserById(userId);

        // 가입하지 않은 user
        if (user == null) {
//            throw new RuntimeException("가입하지 않은 사용자입니다.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "가입하지 않은 사용자입니다.");
        }

        // connected id가 있는 사용자
        if (user.getConnectedId() != null) {
//            throw new IllegalStateException("이미 connected id가 존재하므로 생성할 수 없습니다.");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 connectedId가 존재합니다.");
        }

        try {
            // 1. CODEF 요청 파라미터 구성
            HashMap<String, Object> accountMap = buildAccountMap(request);
            List<HashMap<String, Object>> accountList = List.of(accountMap);
            HashMap<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("accountList", accountList);

            // 2. CODEF API 호출 및 응답 파싱
            String resultJson = codef.createAccount(EasyCodefServiceType.DEMO, parameterMap);
            HashMap<String, Object> responseMap = new ObjectMapper().readValue(resultJson, HashMap.class);

            HashMap<String, Object> resultMap = (HashMap<String, Object>) responseMap.get("result");
            HashMap<String, Object> dataMap = (HashMap<String, Object>) responseMap.get("data");

            String resultCode = (String) resultMap.get("code");
            if (!"CF-00000".equals(resultCode)) {
                throw new RuntimeException("CODEF 응답 실패: " + resultCode);
            }

            // 3. 응답 객체 구성 및 저장
            String connectedId = (String) dataMap.get("connectedId");
            if (connectedId == null || connectedId.isBlank()) {
                throw new RuntimeException("CODEF 성공 응답이지만 connectedId가 누락됨");
            }

            userService.updateConnectedId(userId, connectedId);

            Result result = new Result (
                    (String) resultMap.get("code"),
                    (String) resultMap.get("extraMessage"),
                    (String) resultMap.get("message"),
                    (String) resultMap.get("transactionId")
            );

            ConnectedIdResponse response = new ObjectMapper().convertValue(dataMap, ConnectedIdResponse.class);
            return new CodefStandardResponse<>(result, response);

        } catch (Exception e) {
            //e.printStackTrace();
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
            throw new RuntimeException("비밀번호 암호화 실패", e);
        }
    }

    public CodefStandardResponse<MyCardListResponse> getMyCardList(MyCardListRequest request, Integer userId) {
        User user = userService.getUserById(userId);

        // 가입하지 않은 user
        if (user == null) {
//            throw new RuntimeException("가입하지 않은 사용자입니다.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "가입하지 않은 사용자입니다.");
        }

        String connectedId = user.getConnectedId();

        // connected id가 없는 사용자
        if (connectedId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "connectedId가 존재하지 않습니다. 연동 절차가 필요합니다.");
        }

        HashMap<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("organization", request.organizationCode().getCode());
        parameterMap.put("connectedId", connectedId);
        parameterMap.put("inquiryType", request.inquiryType());

        try {
            String myCardListURL = "/v1/kr/card/p/account/card-list";
            String resultJson = codef.requestProduct(myCardListURL, EasyCodefServiceType.DEMO, parameterMap);

            HashMap<String, Object> responseMap = new ObjectMapper().readValue(resultJson, HashMap.class);
            HashMap<String, Object> resultMap = (HashMap<String, Object>)responseMap.get("result");
            Object dataRaw = responseMap.get("data");

            List<CardInfo> cardList = new ArrayList<>();

            if (dataRaw instanceof List) {
                // 다건 응답
                cardList = new ObjectMapper().convertValue(
                        dataRaw,
                        new TypeReference<List<CardInfo>>() {}
                );
            } else if (dataRaw instanceof Map) {
                // 단건 응답
                CardInfo card = new ObjectMapper().convertValue(
                        dataRaw,
                        CardInfo.class
                );
                cardList.add(card);
            }

            MyCardListResponse response = new MyCardListResponse();
            response.setData(cardList);

            String resultCode = (String) resultMap.get("code");
            if (!"CF-00000".equals(resultCode)) {
                throw new RuntimeException("CODEF 응답 실패: " + resultCode);
            }

            String dataConnectedId = (String) responseMap.get("connectedId");
            if (!connectedId.equals(dataConnectedId)) {
                throw new RuntimeException("요청한 사용자의 정보가 아닙니다.");
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
            //e.printStackTrace();
            throw new RuntimeException("응답 과정 중 오류가 발생했습니다.", e);
        }
    }
}
