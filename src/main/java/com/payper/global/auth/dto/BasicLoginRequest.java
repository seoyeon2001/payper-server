package com.payper.global.auth.dto;

import lombok.Data;

@Data
public class BasicLoginRequest {
    private String username;
    private String password;
}
