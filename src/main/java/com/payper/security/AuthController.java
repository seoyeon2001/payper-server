package com.payper.security;

import com.payper.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // TODO: kakao login
    @PostMapping("/login/kakao")
    public ResponseEntity<LoginResponse> loginKakao(@RequestBody KakaoLoginRequest request) {
        return ResponseEntity.ok(authService.login(request.getCode()));
    }


    // TODO: logout
    /*@GetMapping("/logout")
    public ResponseEntity<> logoutKakao() {
        return null;
    }*/

    // token reissue
}
