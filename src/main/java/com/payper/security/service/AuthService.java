package com.payper.security.service;

import com.google.gson.JsonObject;
import com.payper.security.LoginResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@PropertySource("classpath:application-kakao.properties")
@Log4j2
public class AuthService {

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

    public LoginResponse login(String code) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(code);

        return loginResponse;
    }

    //코드로 카카오auth 서버에서 억세스토큰과 리프레시토큰 얻기
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


        ResponseEntity<JsonObject> response=restTemplate.postForEntity(
                kakaoTokenUrl,
                request,
                JsonObject.class
        );

        if(response.getStatusCode()==HttpStatus.OK){
            return response.getBody().get("access_token").getAsString();
        }
        else{
            throw new RuntimeException("obtain kakao token failed");
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
        params.add("property_keys", "[\"kakao_acount.id\"]");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<JsonObject> response=restTemplate.postForEntity(
                kakaoUserInfoUrl,
                request,
                JsonObject.class
        );

        if(response.getStatusCode()==HttpStatus.OK){
            userInfoMap.put("id", response.getBody().get("id").getAsString());

            return userInfoMap;
        }
        else {
            throw new RuntimeException("obtain kakao user info failed");
        }
    }
}
