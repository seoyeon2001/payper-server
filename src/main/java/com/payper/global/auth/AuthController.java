package com.payper.global.auth;

import com.payper.global.auth.dto.KakaoLoginRequest;
import com.payper.global.auth.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
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
