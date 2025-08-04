package com.payper.global.auth;

import com.payper.global.auth.dto.KakaoLoginRequest;
import com.payper.global.auth.dto.LoginResponse;
import com.payper.global.auth.dto.TokenResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(
        origins = { "http://localhost:5173", "https://payper-client.vercel.app" },
        allowCredentials = "true",
        allowedHeaders = "*",
        methods = { RequestMethod.GET, RequestMethod.POST }
)
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login/kakao")
    public ResponseEntity<LoginResponse> loginKakao(@RequestBody KakaoLoginRequest request, HttpServletResponse response) {
        log.error(request.getCode());
        return ResponseEntity.ok(authService.login(request.getCode(), response));
    }

    @GetMapping("/tokens")
    public ResponseEntity<TokenResponse> reissueTokens(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        return ResponseEntity.ok(authService.reissueTokens(refreshToken, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logOut(HttpServletResponse response) {
        authService.logOut(response);
        return ResponseEntity.ok().build();
    }
}
