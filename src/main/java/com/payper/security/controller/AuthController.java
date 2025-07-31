package com.payper.security.controller;

import com.payper.security.dto.KakaoLoginRequest;
import com.payper.security.dto.LoginResponse;
import com.payper.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {
    private final AuthService authService;

    // TODO: kakao login
    @PostMapping("/login/kakao")
    public ResponseEntity<LoginResponse> loginKakao(@RequestBody KakaoLoginRequest request) {
        log.error(request.getCode());
        return ResponseEntity.ok(authService.login(request.getCode()));
    }


    // TODO: logout
    /*@GetMapping("/logout")
    public ResponseEntity<> logoutKakao() {
        return null;
    }*/

    // TODO: token reissue

}
