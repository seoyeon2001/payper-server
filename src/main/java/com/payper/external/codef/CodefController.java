package com.payper.external.codef;

import com.payper.domain.user.UserService;
import com.payper.external.codef.dto.output.CodefStandardResponse;
import com.payper.external.codef.dto.request.MyCardListRequest;
import com.payper.external.codef.dto.request.ConnectedIdRequest;
import com.payper.external.codef.dto.response.MyCardListResponse;
import com.payper.external.codef.dto.response.ConnectedIdResponse;
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
    public ResponseEntity<CodefStandardResponse<ConnectedIdResponse>> createConnectedId(@RequestBody ConnectedIdRequest request, @AuthenticationPrincipal CustomUser customUser) {
        Integer userId = userService.getUserId(customUser);
        CodefStandardResponse<ConnectedIdResponse> result = codefService.createConnectedId(request, userId);
        return ResponseEntity.ok(result);
    }

    // 보유카드 조회
    @PostMapping("/cards/me")
    public ResponseEntity<CodefStandardResponse<MyCardListResponse>> getMyCardList(@RequestBody MyCardListRequest request, @AuthenticationPrincipal CustomUser customUser) {
        Integer userId = userService.getUserId(customUser);
        CodefStandardResponse<MyCardListResponse> result = codefService.getMyCardList(request, userId);
        return ResponseEntity.ok(result);
    }
}
