package com.payper.external.codef;

import com.payper.domain.user.UserService;
import com.payper.external.codef.dto.output.CodefStandardResponse;
import com.payper.external.codef.dto.request.AddAccountRequest;
import com.payper.external.codef.dto.request.ApprovalListRequest;
import com.payper.external.codef.dto.request.MyCardListRequest;
import com.payper.external.codef.dto.request.CreateAccountRequest;
import com.payper.external.codef.dto.response.ApprovalListResponse;
import com.payper.external.codef.dto.response.AddAccountResponse;
import com.payper.external.codef.dto.response.MyCardListResponse;
import com.payper.external.codef.dto.response.CreateAccountResponse;
import com.payper.global.security.domain.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/codef")
public class CodefController {
    private final CodefService codefService;
    private final UserService userService;

    // 계정 생성 - connected id 생성
    @PostMapping("/account/create")
    public ResponseEntity<CodefStandardResponse<CreateAccountResponse>> createAccount(
            @RequestBody CreateAccountRequest request,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Integer userId = userService.getUserId(customUser);
        CodefStandardResponse<CreateAccountResponse> result = codefService.createAccount(request, userId);
        return ResponseEntity.ok(result);
    }

    // 계정 추가 - connected id에 추가
    @PostMapping("/account/add")
    public ResponseEntity<CodefStandardResponse<AddAccountResponse>> addAccount(
            @RequestBody AddAccountRequest request,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Integer userId = userService.getUserId(customUser);
        CodefStandardResponse<AddAccountResponse> result = codefService.addAccount(request, userId);
        return ResponseEntity.ok(result);
    }

    // 보유카드 조회
    @PostMapping("/cards/me")
    public ResponseEntity<MyCardListResponse> getMyCardList(
            @RequestBody MyCardListRequest request,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Integer userId = userService.getUserId(customUser);
        MyCardListResponse result = codefService.getMyCardList(request, userId);
        return ResponseEntity.ok(result);
    }

    // 승인내역 조회 - 카드사별로 조회 가능
    @PostMapping("/approval")
    public ResponseEntity<ApprovalListResponse> getApprovalList(
            @RequestBody ApprovalListRequest request,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        Integer userId = userService.getUserId(customUser);
        ApprovalListResponse result = codefService.getApprovalList(request, userId);
        return ResponseEntity.ok(result);
    }
}
