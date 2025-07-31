package com.payper.user.controller;

import com.payper.security.domain.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Log4j2
public class UserController {

    @GetMapping("")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal CustomUser customuser) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        log.info("전체 SecurityContext 상태: {}", SecurityContextHolder.getContext());

        log.info("현재 SecurityContext 인증 객체: {}", auth);
        log.info("현재 Principal: {}", auth != null ? auth.getPrincipal() : "null");

        if (customuser == null) {
            log.warn("@AuthenticationPrincipal customuser는 null임");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패");
        }
        return ResponseEntity.ok().build();
    }
}
