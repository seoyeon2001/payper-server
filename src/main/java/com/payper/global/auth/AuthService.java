package com.payper.global.auth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.payper.domain.user.dto.CreateUserRequest;
import com.payper.global.auth.dto.LoginResponse;
import com.payper.global.auth.dto.TokenResponse;
import com.payper.global.auth.exception.InvalidRefreshTokenException;
import com.payper.global.auth.exception.UserCreationFailedException;
import com.payper.global.security.util.OAuthProvider;
import com.payper.global.security.util.JwtProcessor;
import com.payper.domain.user.UserMapper;
import com.payper.domain.user.domain.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Component
public class AuthService {
    private final JwtProcessor jwtProcessor;
    private final UserMapper userMapper;

    private final RestTemplate restTemplate;

    @Value("${kakao.client.id}")
    private String kakaoClientId; //appkey

    @Value("${kakao.client.secret}")
    private String kakaoClientSecret;

    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;

    @Value("${kakao.token.url}")
    private String kakaoTokenUrl;

    @Value("${kakao.user.info.url}")
    private String kakaoUserInfoUrl;

    public LoginResponse login(String code, HttpServletResponse response) {// 우리 서비스의 로그인 응답 객체 반환
        String kakaoAccessToken = getReturnAccessToken(code);

        Map<String, Object> kakaoUserInfo = getMemberInfo(kakaoAccessToken);
        String kakaoId = kakaoUserInfo.get("kakao_id").toString();
        String kakaoNickname = kakaoUserInfo.get("nickname").toString();

        Integer userId = userMapper.findByOauthProviderAndOauthId(OAuthProvider.KAKAO.name(), kakaoId);

        if(userId==null){
            CreateUserRequest newUser=CreateUserRequest.toDTO(
                    OAuthProvider.KAKAO.name(),kakaoId,kakaoNickname
            );

            if(userMapper.createUser(newUser)!=1){
                log.error("유저 생성 Failed "+kakaoNickname);

                throw new UserCreationFailedException(kakaoNickname);
            }

            userId = newUser.getUserId();
        }

        String accessToken = jwtProcessor.generateAccessToken(userId);

        String refreshToken = jwtProcessor.generateRefreshToken(userId);

        storeRefreshTokenInCookie(response, refreshToken);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(accessToken);

        return loginResponse;
    }

    //코드로 카카오auth 서버에서 억세스토큰과 리프레시토큰 얻기.
    //카카오 auth accessToken은 여기서만 쓰인다.
    //카카오 auth accessToken 카카오 계정 고유 ID를 얻기 위함이 크다!
    private String getReturnAccessToken(String code) {
        // String refreshToken = "";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("client_secret", kakaoClientSecret);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        // HttpEntity 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        //RestTemplate은 기본적으로 jackson을 사용. 그래서 JsonObject로 받으면 안돼.
        ResponseEntity<String> response = restTemplate.postForEntity(
                kakaoTokenUrl,
                request,
                String.class
        );


        if (response.getStatusCode() == HttpStatus.OK) {
            // JSON 파싱을 위해 Gson 사용
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);

            if (jsonObject.has("access_token")) {
                return jsonObject.get("access_token").getAsString();
            } else {
                log.error("access_token not in response: {}", response.getBody());
                throw new RuntimeException("access_token not found in response");
            }
        } else {
            log.error("request kakao access token fail. response status: {}, response: {}",
                    response.getStatusCode(), response.getBody());
            throw new RuntimeException("obtain kakao token failed: " + response.getStatusCode());
        }
    }

    //억세스토큰으로 카카오 사용자 정보 얻기.
    // payper서비스에 가입돼 있는지 확인하기 위해
    private Map<String, Object> getMemberInfo(String accessToken) {
        Map<String, Object> userInfoMap = new HashMap<String, Object>();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("property_keys", "[\"kakao_account.profile\"]");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        //HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                kakaoUserInfoUrl,
                request,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            // JSON 파싱을 위해 Gson 사용
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);

            // ID 추출
            if (jsonObject.has("id")) {
                userInfoMap.put("kakao_id", jsonObject.get("id").getAsString());
            }

            // 닉네임 추출
            if (jsonObject.has("kakao_account")) {
                JsonObject kakaoAcount = jsonObject.getAsJsonObject("kakao_account");
                if(kakaoAcount.has("profile")){
                    JsonObject profile = kakaoAcount.getAsJsonObject("profile");
                    if(profile.has("nickname")){
                        userInfoMap.put("nickname", profile.get("nickname").getAsString());
                    }
                }
            }

            return userInfoMap;

        } else {
            log.error("request kakao user info failed. response status: {}, response: {}",
                    response.getStatusCode(), response.getBody());
            throw new RuntimeException("obtain kakao user info failed: " + response.getStatusCode());
        }
    }
    public TokenResponse reissueTokens(String oldRefreshToken, HttpServletResponse response) {
        if (oldRefreshToken != null && !jwtProcessor.validateJwtToken(oldRefreshToken)) {
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

    private void storeRefreshTokenInCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setMaxAge(jwtProcessor.getRefreshTokenMaxAgeInSeconds());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}
