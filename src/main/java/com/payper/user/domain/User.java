package com.payper.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Integer userId; // 사용자 id
    private String oauthProvider; // 소셜 로그인 제공사
    private String oauthId; // 소셜 id
    private String userName; // 사용자 이름
    private List<UserCard> userCardList; // 사용자가 소유한 카드 목록 리스트
}