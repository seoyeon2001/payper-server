package com.payper.security.dto;

import com.payper.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;
    //private String refreshToken;

    //다른 프로젝트(프론트에서 유저 정보를 가지고 관리하는)경우, 토큰과 유저 정보를 넘겨주지만
    //payper는 프론트에서 유저 정보를 갖지 않고, 유저 정보 활용시 유저 정보에 대한 api를 다시 호출해야함. 매우 restful
    //해당 프로젝트인 경우, LoginResponse에 채워줄 유저 정보를 구할 필요 없음!
    //User userInfo;
}
