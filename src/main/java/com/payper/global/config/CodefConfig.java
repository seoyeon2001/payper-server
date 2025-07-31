package com.payper.global.config;

import io.codef.api.EasyCodef;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CodefConfig {
    @Value("${demo.client.id}")
    private String DEMO_CLIENT_ID;

    @Value("${demo.client.secret}")
    private String DEMO_CLIENT_SECRET;

    @Value("${public.key}")
    private String PUBLIC_KEY;

    @Bean
    public EasyCodef easyCodef() {
        EasyCodef codef = new EasyCodef();

        // 데모 클라이언트 정보 설정
        codef.setClientInfoForDemo(DEMO_CLIENT_ID, DEMO_CLIENT_SECRET);

        // RSA 공개키 설정
        codef.setPublicKey(PUBLIC_KEY);

        return codef;
    }
}

