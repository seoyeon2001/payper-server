package com.payper.domain.report;

import com.payper.domain.user.UserService;
import com.payper.domain.user.dto.UserReportResponse;
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

    @GetMapping("/me")
    public ResponseEntity<UserReportResponse> getReport(
            @AuthenticationPrincipal CustomUser customUser,
            @RequestParam(value = "month") String month) {
        Integer userId = userService.getUserId(customUser);
        return ResponseEntity.ok(reportService.analyzeMonth(userId, month));
    }
}
