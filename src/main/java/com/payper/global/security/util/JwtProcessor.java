package com.payper.global.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProcessor {
    private static final long ACCESS_TOKEN_MAX_AGE = 7L * 24 * 60 * 60 * 1000;
    private static final long REFRESH_TOKEN_MAX_AGE = 30L * 24 * 60 * 60 * 1000; // 30일

    //개발시 키
    final private String secretKey = "abcdefghijklmnopqrstuvxyzabcdefghijklmnopqrstuvxyzabcdefghijklmnopqrstuvxyzabcdefghijklmnopqrstuvxyz";
    final private Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

    //운영시 키
    //private Key key=Keys.secretKeyFor(SignatureAlgorithm.HS256);

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

    //유효하지 않을 경우 예외 발생함.
    public boolean validateJwtToken(String token) {
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);

        return true;
    }

    public int getRefreshTokenMaxAgeInSeconds() {
        return (int) (REFRESH_TOKEN_MAX_AGE / 1000);
    }
}
