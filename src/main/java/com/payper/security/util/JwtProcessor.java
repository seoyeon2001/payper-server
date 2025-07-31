package com.payper.security.util;

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
    static private final long TOKEN_VALID_MILISECOND = 60 * 60 * 60 * 1000;

    //개발시 키
    final private String secretKey = "abcdefghijklmnopqrstuvxyzabcdefghijklmnopqrstuvxyzabcdefghijklmnopqrstuvxyzabcdefghijklmnopqrstuvxyz";
    final private Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

    //운영시 키
    //private Key key=Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateJwtToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(
                        new Date().getTime() + TOKEN_VALID_MILISECOND)
                )
                .signWith(key)
                .compact();
    }

    public String generateJwtToken(Integer subject) {
        return Jwts.builder()
                .setSubject(subject.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(
                        new Date().getTime() + TOKEN_VALID_MILISECOND)
                )
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
}
