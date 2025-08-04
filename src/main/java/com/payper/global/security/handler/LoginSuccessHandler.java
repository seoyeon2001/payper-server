package com.payper.global.security.handler;


import com.payper.global.security.domain.CustomUser;
import com.payper.global.auth.dto.LoginResponse;
import com.payper.global.security.util.JsonResponse;
import com.payper.global.security.util.JwtProcessor;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtProcessor jwtProcessor;
    private final JsonResponse jsonResponse;

    private LoginResponse makeAuthResult(CustomUser user) {//얻은 UserDetails가 들어올 예정
        String userId = user.getUsername( );
        String token = jwtProcessor.generateJwtToken(userId); // 토큰 생성
        // 토큰 + 사용자 기본 정보 (사용자명, ...)를 묶어서 AuthResultDTO 구성
        return new LoginResponse(token);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        CustomUser user = (CustomUser) authentication.getPrincipal( ); // 인증 결과 Principal
        LoginResponse result = makeAuthResult(user); // 인증 성공 결과를 JSON으로 직접 응답
        jsonResponse.send(response, result); //토큰, 사용자 기본정보 json담아 보내
    }
}
