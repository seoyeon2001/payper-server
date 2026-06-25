package com.payper.global.security.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProcessor {
    private static final long ACCESS_TOKEN_MAX_AGE = 7L * 24 * 60 * 60 * 1000;
    private static final long REFRESH_TOKEN_MAX_AGE = 30L * 24 * 60 * 60 * 1000; // 30일

    @Value("${token.secret.key}")
    private String secretKey;

    private Key key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Integer userId) {
        return generateJwtToken(userId, ACCESS_TOKEN_MAX_AGE);
    }

    public String generateRefreshToken(Integer userId) {
        return generateJwtToken(userId, REFRESH_TOKEN_MAX_AGE);
    }

    public String generateJwtToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(
                        new Date().getTime() + ACCESS_TOKEN_MAX_AGE)
                )
                .signWith(key)
                .compact();
    }

    public String generateJwtToken(Integer subject, long maxAge) {
        return Jwts.builder()
                .setSubject(subject.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + maxAge))
                .signWith(key)
                .compact();
    }

    public Integer getUserId(String token) {
        return Integer.valueOf(
                Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject()
        );
    }

    public String getUserIdString(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    //유효하지 않을 경우 예외 발생함.
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int getRefreshTokenMaxAgeInSeconds() {
        return (int) (REFRESH_TOKEN_MAX_AGE / 1000);
    }
}
