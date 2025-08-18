package com.payper.domain.userCardTransaction;

import com.payper.domain.user.UserService;
import com.payper.external.codef.dto.response.ApprovalListResponse;
import com.payper.global.security.domain.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserCardTransactionController {
    private final UserService userService;
    private final UserCardTransactionService userCardTransactionService;

    /**
     * 오늘을 기준으로 7일전까지의 카드 사용 내역 불러오기 ( 최근 사용 내역 불러오기 )
     */
    @GetMapping("/transaction/recent")
    public ResponseEntity<ApprovalListResponse> getRecentTransactions(
            @RequestParam(defaultValue = "7") int days,
            @AuthenticationPrincipal CustomUser customUser) {
        Integer userId = userService.getUserId(customUser);

        ApprovalListResponse result = userCardTransactionService.getRecentTransactions(userId, days);
        return ResponseEntity.ok(result);
    }
}
