package com.payper.global.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 기반 인증을 처리하는 필터.
 * 매 요청마다 실행되며, JWT 토큰이 있을 경우 이를 기반으로 인증을 시도함.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청에서 JWT를 추출하여 Authentication 객체로 변환
        Authentication authenticationRequest = authenticationConverter.convert(request);
        // JWT가 없거나 유효하지 않으면 다음 필터로 넘김
        if(authenticationRequest == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // AuthenticationManager를 통해 인증 시도
            Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);
            // 인증 실패 또는 인증 객체가 없을 경우, 다음 필터로 넘김
            if (authenticationResult == null || !authenticationResult.isAuthenticated()) {
                filterChain.doFilter(request, response);
                return;
            }

            // 인증 성공 시 SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authenticationResult);
        } catch (Exception e) {
            log.error("Authentication failed.", e);
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}
