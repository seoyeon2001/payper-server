package com.payper.security;

import com.payper.user.domain.User;
import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    //private String refreshToken;
}
