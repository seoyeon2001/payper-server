package com.payper.global.auth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.payper.global.auth.dto.LoginResponse;
import com.payper.global.security.util.OAuthProvider;
import com.payper.global.security.util.JwtProcessor;
import com.payper.domain.user.UserMapper;
import com.payper.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@PropertySource("classpath:application-kakao.properties")
@Log4j2
@RequiredArgsConstructor
@Component
public class AuthService {
    private final JwtProcessor jwtProcessor;
    private final UserMapper userMapper;

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

    public LoginResponse login(String code) {// 우리 서비스의 로그인 응답 객체 반환
        String kakaoAccessToken = getReturnAccessToken(code);

        Map<String, Object> kakaoUserInfo = getMemberInfo(kakaoAccessToken);
        String kakaoId = kakaoUserInfo.get("kakao_id").toString();
        String kakaoNickname = kakaoUserInfo.get("nickname").toString();

        User user = userMapper.findByOauthProviderAndOauthId(OAuthProvider.KAKAO.name(), kakaoId);

        if (user == null) {
            User newUser = User.builder()
                    .oauthProvider(OAuthProvider.KAKAO.name())
                    .oauthId(kakaoId)
                    .nickname(kakaoNickname)
                    .isDeleted(false)
                    .createdAt(new Date())
                    .deletedAt(null)
                    .lastModifiedAt(new Date())
                    .build();

            int createResult=userMapper.createUser(newUser);

            //log.error("create user result:"+createResult);

            user = newUser;
        }

        //log.error(user.toString());

        String payperJwtToken = jwtProcessor.generateJwtToken(user.getUserId());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(payperJwtToken);

        return loginResponse;
    }

    //코드로 카카오auth 서버에서 억세스토큰과 리프레시토큰 얻기.
    //카카오 auth accessToken은 여기서만 쓰인다.
    //카카오 auth accessToken 카카오 계정 고유 ID를 얻기 위함이 크다!
    private String getReturnAccessToken(String code) {
        // String refreshToken = "";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("client_secret", kakaoClientSecret);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        // HttpEntity 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        //log.error(request.toString());

        //RestTemplate은 기본적으로 jackson을 사용. 그래서 JsonObject로 받으면 안돼.
        ResponseEntity<String> response = restTemplate.postForEntity(
                kakaoTokenUrl,
                request,
                String.class
        );

        //log.error(response.getStatusCode());
        //log.error(response.getBody());

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

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(
                kakaoUserInfoUrl,
                request,
                String.class
        );

        //log.error("response status: {}", response.getStatusCode());
        //log.error("response body: {}", response.getBody());

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

            //log.error("user info: {}", userInfoMap);
            return userInfoMap;

        } else {
            log.error("request kakao user info failed. response status: {}, response: {}",
                    response.getStatusCode(), response.getBody());
            throw new RuntimeException("obtain kakao user info failed: " + response.getStatusCode());
        }
    }
}
