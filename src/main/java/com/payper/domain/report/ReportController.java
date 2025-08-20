package com.payper.domain.report;

import com.payper.domain.user.UserService;
import com.payper.domain.user.dto.UserReportResponse;
import com.payper.domain.userCardTransaction.UserCardTransactionService;
import com.payper.external.codef.dto.response.ApprovalListResponse;
import com.payper.global.security.domain.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final UserService userService;
    private final ReportService reportService;
    private final UserCardTransactionService userCardTransactionService;

    @GetMapping("/me")
    public ResponseEntity<UserReportResponse> getReport(
            @AuthenticationPrincipal CustomUser customUser,
            @RequestParam(value = "month") String month) {
        Integer userId = userService.getUserId(customUser);
        return ResponseEntity.ok(reportService.analyzeMonth(userId, month));
    }

    /**
     * 오늘을 기준으로 7일전까지의 카드 사용 내역 불러오기 ( 최근 사용 내역 불러오기 )
     */
    @GetMapping("/transaction/me")
    public ResponseEntity<ApprovalListResponse> getRecentTransactions(
            @RequestParam(defaultValue = "7") int days,
            @AuthenticationPrincipal CustomUser customUser) {
        Integer userId = userService.getUserId(customUser);

        ApprovalListResponse result = userCardTransactionService.getRecentTransactions(userId, days);
        return ResponseEntity.ok(result);
    }
}
