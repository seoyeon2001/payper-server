package com.payper.external.codef;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payper.domain.user.UserCardMapper;
import com.payper.domain.userCardTransaction.UserCardTransactionService;
import com.payper.domain.card.mapper.CardMapper;
import com.payper.domain.user.domain.UserCard;
import com.payper.external.codef.dto.CardRegistrationResult;
import com.payper.external.codef.dto.RegistrationStatus;
import com.payper.external.codef.dto.output.ApprovalInfo;
import com.payper.external.codef.dto.output.CardInfo;
import com.payper.external.codef.dto.output.CodefStandardResponse;
import com.payper.external.codef.dto.output.Result;
import com.payper.external.codef.dto.request.AddAccountRequest;
import com.payper.external.codef.dto.request.ApprovalListRequest;
import com.payper.external.codef.dto.request.CreateAccountRequest;
import com.payper.external.codef.dto.request.MyCardListRequest;
import com.payper.external.codef.dto.response.AddAccountResponse;
import com.payper.external.codef.dto.response.ApprovalListResponse;
import com.payper.external.codef.dto.response.CreateAccountResponse;
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
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodefService {
    private final EasyCodef codef;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final CodefMapper codefMapper;
    private final CardSimilarityService cardSimilarityService;
    private final UserCardTransactionService userCardTransactionService;
    private final CardMapper cardMapper;
    private final UserCardMapper userCardMapper;
    private static final String MY_CARD_LIST_URL = "/v1/kr/card/p/account/card-list";
    private static final String APPROVAL_LIST_URL = "/v1/kr/card/p/account/approval-list";

    // 테스트 용
//    private static FilteredCardByCompanyName fc(int id, String name, String type) {
//        FilteredCardByCompanyName c = new FilteredCardByCompanyName();
//        c.setId(id);
//        c.setName(name);
//        c.setType(type);
//        return c;
//    }

    // 계정 생성 - connected id 발급
    public CodefStandardResponse<CreateAccountResponse> createAccount(CreateAccountRequest request, Integer userId) {
        String connectedId = userService.getConnectedIdById(userId);

        // connected id가 있는 사용자
        if (connectedId != null) {
//        if (connectedId != null && !connectedId.isEmpty()) {
            throw new AlreadyLinkedCodefAccountException();
        }

        try {
            // 1. CODEF 요청 파라미터 구성
            HashMap<String, Object> accountMap = buildAccountMap (
                    request.organizationName().getCode(),
                    request.companyId(),
                    request.companyPassword()
            );

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
            CreateAccountResponse response = objectMapper.convertValue(dataMap, CreateAccountResponse.class);
            return new CodefStandardResponse<>(result, response);

        } catch (Exception e) {
            throw new RuntimeException("계정 등록 중 오류가 발생했습니다.(ConnectedId 생성 실패)", e);
        }
    }


    // 계정 추가
    public CodefStandardResponse<AddAccountResponse> addAccount(AddAccountRequest request, Integer userId) {
        String connectedId = userService.getConnectedIdById(userId);

        // 사용자는 존재하는데, connected id가 없는 경우
        if (connectedId == null || connectedId.isEmpty()) {
            throw new CodefAccountNotLinkedException();
        }

        try {
            // 1. CODEF 요청 파라미터 구성
            HashMap<String, Object> accountMap = buildAccountMap(
                    request.organizationName().getCode(),
                    request.companyId(),
                    request.companyPassword()
            );

            List<HashMap<String, Object>> accountList = List.of(accountMap);
            HashMap<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("accountList", accountList);
            parameterMap.put("connectedId", connectedId);

            // 2. CODEF API 호출 및 응답 파싱
            String resultJson = codef.addAccount(EasyCodefServiceType.DEMO, parameterMap);

            HashMap<String, Object> responseMap = objectMapper.readValue(resultJson, HashMap.class);

            HashMap<String, Object> resultMap = (HashMap<String, Object>) responseMap.get("result");
            HashMap<String, Object> dataMap = (HashMap<String, Object>) responseMap.get("data");

            String resultCode = (String) resultMap.get("code");
            checkCodefResultCode(resultCode);

            // 3. 응답 객체 구성 및 저장
            String resultConnectedId = (String) dataMap.get("connectedId");
            if (resultConnectedId == null || resultConnectedId.isBlank()) {
                throw new MissingConnectedIdInCodefResponseException();
            }

            // CODEF 응답 Connected ID와 DB Connected ID가 다르면 에러
            if (!resultConnectedId.equals(connectedId)) {
                throw new ConnectedIdMismatchException();
            }

            // Result 객체 생성
            Result result = createResult(resultMap);
            AddAccountResponse response = objectMapper.convertValue(dataMap, AddAccountResponse.class);
            return new CodefStandardResponse<>(result, response);

        } catch (Exception e) {
            throw new RuntimeException("계정 추가 중 오류가 발생했습니다.", e);
        }
    }

    private HashMap<String, Object> buildAccountMap(String organizationCode, String companyId, String companyPassword) {
        HashMap<String, Object> accountMap = new HashMap<>();
        accountMap.put("countryCode", "KR"); // 한국
        accountMap.put("businessType", "CD"); // 카드
        accountMap.put("clientType", "P"); // 개인
        accountMap.put("loginType", "1"); // 아이디 비번 로그인
        accountMap.put("organization", organizationCode);
        accountMap.put("id", companyId);

        try {
            accountMap.put("password", EasyCodefUtil.encryptRSA(companyPassword, codef.getPublicKey()));
        } catch (Exception e) {
            throw new EncryptPasswordFailedException();
        }

        return accountMap;
    }

    public MyCardListResponse getMyCardList(MyCardListRequest request, Integer userId) {
        String connectedId = userService.getConnectedIdById(userId);

        // 사용자는 존재하는데, connected id가 없는 경우
        if (connectedId == null || connectedId.isEmpty()) {
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
            List<CardInfo> apiCardList = processResponse(dataRaw, CardInfo.class);
//            if (apiCardList == null) apiCardList = Collections.emptyList();

/*            // 테스트용 API 응답 카드 리스트
            List<CardInfo> apiCardList = new ArrayList<>();
            apiCardList.add(new CardInfo(
                    "CREDIT",            // resCardType
                    "2028-12",           // resValidPeriod
                    "신한 라이킷 FUN+ 카드", // resCardName
                    "Y",                 // resTrafficYN
                    "2023-08-01",        // resIssueDate
                    "홍길동",              // resUserNm
                    "N",                 // resSleepYN
                    "4578-12**-****-3456", // resCardNo
                    "ACTIVE",            // resState
                    "https://example.com/img/shinhan_likit_fun_plus.png" // resImageLink
            ));
            apiCardList.add(new CardInfo(
                    "CHECK",
                    "2027-05",
                    "신한 라이킷 FUN+ 체크카드",
                    "Y",
                    "2024-01-15",
                    "홍길동",
                    "N",
                    "4578-34**-****-7890",
                    "ACTIVE",
                    "https://example.com/img/shinhan_likit_fun_plus_check.png"
            ));
            apiCardList.add(new CardInfo(
                    "CREDIT",
                    "2029-03",
                    "KB국민 탄탄대로 올쇼핑",
                    "N",
                    "2022-10-03",
                    "김철수",
                    "N",
                    "5522-90**-****-1122",
                    "ACTIVE",
                    "https://example.com/img/kb_tantandae_olshopping.png"
            ));
            apiCardList.add(new CardInfo(
                    "CHECK",
                    "2026-11",
                    "삼성 taptap CHECK",
                    "Y",
                    "2021-07-20",
                    "이영희",
                    "Y",
                    "5333-22**-****-3344",
                    "SLEEP",
                    "https://example.com/img/samsung_taptap_check.png"
            ));
            apiCardList.add(new CardInfo(
                    "CREDIT",
                    "2030-01",
                    "현대 ZERO Edition2",
                    "N",
                    "2020-12-12",
                    "박민수",
                    "N",
                    "4111-00**-****-5566",
                    "ACTIVE",
                    "https://example.com/img/hyundai_zero_edition2.png"
            ));*/

            // 내가 요청한 카드사 이름
            String companyName = request.organizationName().name();
//            String companyName = "삼성카드";

            // 카드사가 소유한 카드 리스트 조회 - 카드사이름 기반 DB 필터링
            List<FilteredCardByCompanyName> dbCardList = codefMapper.findCardByCompanyName(companyName);

//            // 테스트 시: DB 조회 결과 대신 하드코딩된 목록 사용
//            List<FilteredCardByCompanyName> dbCardList = new ArrayList<>(List.of(
//                    fc(1,  "신한 라이킷 FUN+ 카드",      "CREDIT"),
//                    fc(2,  "신한 라이킷 FUN+ 체크카드",   "CHECK"),
//                    fc(3,  "KB국민 탄탄대로 올쇼핑",       "CREDIT"),
//                    fc(4,  "KB국민 노리 체크카드",         "CHECK"),
//                    fc(5,  "삼성 taptap O",                "CREDIT"),
//                    fc(6,  "삼성 taptap CHECK",            "CHECK"),
//                    fc(7,  "우리 카드의정석 POINT",         "CREDIT"),
//                    fc(8,  "우리 카드의정석 체크",          "CHECK"),
//                    fc(9,  "하나 1Q Pay 카드",             "CREDIT"),
//                    fc(10, "하나 1Q 체크카드",             "CHECK"),
//                    fc(11, "롯데 I'm WONDERFUL",           "CREDIT"),
//                    fc(12, "롯데 포인트플러스 체크",        "CHECK"),
//                    fc(13, "NH농협 올바른 FLEX",           "CREDIT"),
//                    fc(14, "NH농협 올바른 체크카드",        "CHECK"),
//                    fc(15, "현대 ZERO Edition2",           "CREDIT"),
//                    fc(16, "현대 ZERO Edition2 체크",       "CHECK"),
//                    fc(17, "IBK i-ONE 카드",               "CREDIT"),
//                    fc(18, "IBK i-ONE 체크카드",           "CHECK"),
//                    fc(19, "BC 바로카드",                  "CREDIT"),
//                    fc(20, "BC 바로 체크카드",             "CHECK")
//            ));

            if (dbCardList == null || dbCardList.isEmpty()) {
                List<CardRegistrationResult> noMatch = new ArrayList<>();
                for (CardInfo apiCard : apiCardList) {
                    noMatch.add(new CardRegistrationResult(
                            apiCard.getResCardName(), null, null,
                            RegistrationStatus.NO_MATCH,
                            "해당 카드사의 카드가 없습니다."
                    ));
                }
                return new MyCardListResponse(apiCardList, noMatch, createSummaryMessage(noMatch));
            }

            // 내 카드로 등록 처리
            List<CardRegistrationResult> registrationResults = processCardRegistrations(apiCardList, dbCardList, userId);

            // 요약 메시지 생성
            String summary = createSummaryMessage(registrationResults);

            return new MyCardListResponse(apiCardList, registrationResults, summary);

        } catch (Exception e) {
            throw new RuntimeException("응답 과정 중 오류가 발생했습니다.", e);
        }
    }

    public ApprovalListResponse getApprovalList(ApprovalListRequest request, Integer userId) {
        String connectedId = userService.getConnectedIdById(userId);

        // 사용자는 존재하는데, connected id가 없는 경우
        if (connectedId == null || connectedId.isEmpty()) {
            throw new CodefAccountNotLinkedException();
        }

        HashMap<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("organization", request.organizationName().getCode());
        parameterMap.put("connectedId", connectedId);
        parameterMap.put("startDate", request.startDate());
        parameterMap.put("endDate", request.endDate());
        parameterMap.put("orderBy", "0"); // 최신순
        parameterMap.put("inquiryType", "1"); // 카드사 별로 조회
        parameterMap.put("memberStoreInfoType", request.memberStoreInfoType());

        try {
            String resultJson = codef.requestProduct(APPROVAL_LIST_URL, EasyCodefServiceType.DEMO, parameterMap);
            log.info(resultJson);

            // Json -> java 객체
            Map<String, Object> responseMap = objectMapper.readValue(resultJson, new TypeReference<>() {});

            // 검증 로직 (result, connectedId)
            validateCodefResponse(responseMap, connectedId);

            // 승인 내역 데이터 반환 - 저장과 관련 없이 받아온 값 전부 return
            Object dataRaw = responseMap.get("data");
            List<ApprovalInfo> approvalList = processResponse(dataRaw, ApprovalInfo.class);
            log.info(approvalList.toString());

            // 해당 유저가 가진 해당 카드사의 카드 목록 불러오기
            String cardCompany = request.organizationName().name();
            List<UserCard> myCardList = userCardMapper.getUserCardByUserIdAndCardCompany(userId, cardCompany);
            log.info("소유하고 있는 {} 카드 목록입니다 {}", cardCompany, myCardList);

            // 승인 내역 저장
            userCardTransactionService.saveApprovalTransactions(approvalList, userId, myCardList);

            ApprovalListResponse response = new ApprovalListResponse(approvalList);
            return response;

        } catch (Exception e) {
            throw new RuntimeException("응답 과정 중 오류가 발생했습니다.", e);
        }
    }

    // 단건 응답, 다건 응답 List 반환 통일
    private <T> List<T> processResponse(Object dataRaw, Class<T> targetClass) {
        if (dataRaw == null) {
            return Collections.emptyList();
        }

        try {
            if (dataRaw instanceof List) {
                // 다건 응답: data가 배열 [{}, {}]
                return objectMapper.convertValue(dataRaw,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, targetClass));

            } else {
                // 단건 응답: data가 단일 객체 {}
                T item = objectMapper.convertValue(dataRaw, targetClass);
                return List.of(item);
            }
        }  catch (Exception e) {
            log.error("Failed to convert {} data: {}", targetClass.getSimpleName(), dataRaw, e);
            throw new RuntimeException(targetClass.getSimpleName() + " 정보 변환 실패", e);
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
        log.info(resultMap.toString());
        String resultCode = (String) resultMap.get("code");
        checkCodefResultCode(resultCode);

        // ConnectedId 검증
        String dataConnectedId = (String) responseMap.get("connectedId");
        if (!connectedId.equals(dataConnectedId)) {
            throw new MismatchedConnectedIdException(connectedId);
        }
    }

    // 카드 등록 일괄 처리
    private List<CardRegistrationResult> processCardRegistrations(List<CardInfo> apiCardList, List<FilteredCardByCompanyName> dbCardList, Integer userId) {
        List<CardRegistrationResult> results = new ArrayList<>();

        // DB 카드 이름만 추출
        List<String> dbCardNames = dbCardList.stream()
                .map(FilteredCardByCompanyName::getName)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        // 개별 카드 등록 프로세스
        for (CardInfo apiCard : apiCardList) {
            CardRegistrationResult result = registerUserCardWithResult(
                    apiCard.getResCardName(), dbCardNames, userId,
                    apiCard.getResCardNo());
            results.add(result);

            log.info("카드 등록 처리: {} - {}", result.getApiCardName(), result.getStatus().getDescription());
        }

        return results;
    }

    // 개별 카드 등록 (결과 반환)
    @Transactional
    protected CardRegistrationResult registerUserCardWithResult(String apiCardName, List<String> dbCardNames, Integer userId, String apiCardNo) {
        try {
            // 유사도 매칭 - 가장 유사한 1개 추출
            List<CardSimilarityResponse> recommendations =
                    cardSimilarityService.recommendSimilarCards(apiCardName, 1, dbCardNames);

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
                        "카드 정보 오류 - cardId 찾기 실패");
            }

            // 등록된 이력 상태 확인 및 처리
            boolean isAlreadyActive = cardMapper.existsUserCard(userId, cardId);

            if (isAlreadyActive) { // 이미 등록된 경우
                return new CardRegistrationResult(apiCardName, matchedCardName, cardId,
                        RegistrationStatus.ALREADY_EXISTS,
                        "이미 등록된 카드입니다.");
            }

            // 내 카드에서 삭제되었던 경우
            if (cardMapper.isPreviouslyDeletedUserCard(userId, cardId)) {
                String lastNumber = apiCardNo.substring(apiCardNo.length() - 3);
                cardMapper.restoreUserCardWithNumber(userId, cardId, lastNumber);
                cardMapper.registerCodefCardName(cardId, apiCardName);
                return new CardRegistrationResult(apiCardName, matchedCardName, cardId,
                        RegistrationStatus.RESTORED,
                        "삭제된 카드를 복원했습니다.");
            } else {
                String lastNumber = apiCardNo.substring(apiCardNo.length() - 3);
                cardMapper.registerMyCardWithNumber(userId, cardId, lastNumber);
                cardMapper.registerCodefCardName(cardId, apiCardName);
                return new CardRegistrationResult(apiCardName, matchedCardName, cardId,
                        RegistrationStatus.SUCCESS,
                        "카드가 등록되었습니다.");
            }

        } catch (Exception e) {
            log.error("카드 등록 처리 중 오류: apiCardName: {}, userId: {}", apiCardName, userId);
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
