package com.payper.global.auth;

import com.payper.domain.user.UserMapper;
import com.payper.domain.user.domain.User;
import com.payper.domain.user.dto.UserResponse;
import com.payper.global.auth.dto.BasicLoginRequest;
import com.payper.global.auth.dto.SignupRequest;
import com.payper.global.auth.dto.TokenResponse;
import com.payper.global.auth.exception.DuplicateUsernameException;
import com.payper.global.auth.exception.InvalidAuthRequestException;
import com.payper.global.auth.exception.InvalidCredentialsException;
import com.payper.global.auth.exception.InvalidRefreshTokenException;
import com.payper.global.security.util.JwtProcessor;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final JwtProcessor jwtProcessor;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse signup(SignupRequest request) {
        if (request == null) {
            throw new InvalidAuthRequestException("request");
        }

        String username = requireText(request.getUsername(), "username");
        String password = requireText(request.getPassword(), "password");
        String nickname = requireText(request.getNickname(), "nickname");

        if (userMapper.countByUsername(username) > 0) {
            throw new DuplicateUsernameException(username);
        }

        User user = User.createLocalUser(
                username,
                passwordEncoder.encode(password),
                nickname
        );
        userMapper.save(user);

        return UserResponse.toDto(user);
    }

    public TokenResponse login(BasicLoginRequest request, HttpServletResponse response) {
        if (request == null) {
            throw new InvalidAuthRequestException("request");
        }

        String username = requireText(request.getUsername(), "username");
        String password = requireText(request.getPassword(), "password");

        User user = userMapper.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtProcessor.generateAccessToken(user.getUserId());
        String refreshToken = jwtProcessor.generateRefreshToken(user.getUserId());
        storeRefreshTokenInCookie(response, refreshToken);

        return new TokenResponse(accessToken);
    }

    public TokenResponse reissueTokens(String oldRefreshToken, HttpServletResponse response) {
        if (oldRefreshToken == null || !jwtProcessor.validateJwtToken(oldRefreshToken)) {
            throw new InvalidRefreshTokenException(oldRefreshToken); // TODO: 핸들러 작성
        }

        Integer userId = jwtProcessor.getUserId(oldRefreshToken);
        String newAccessToken = jwtProcessor.generateAccessToken(userId);
        String newRefreshToken = jwtProcessor.generateRefreshToken(userId);

        storeRefreshTokenInCookie(response, newRefreshToken);

        return new TokenResponse(newAccessToken);
    }

    public void logOut(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidAuthRequestException(fieldName);
        }
        return value.trim();
    }

    private void storeRefreshTokenInCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setMaxAge(jwtProcessor.getRefreshTokenMaxAgeInSeconds());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}
