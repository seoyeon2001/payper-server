package com.payper.global.auth.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.security.authentication.BadCredentialsException;

import javax.servlet.http.HttpServletRequest;

@Data
public class KakaoLoginRequest {
    private String code;

    public static KakaoLoginRequest of(HttpServletRequest request) {
        ObjectMapper om = new ObjectMapper();
        try{
            return om.readValue(request.getInputStream(), KakaoLoginRequest.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadCredentialsException("Kakao Login Request failed");
        }
    }
}
