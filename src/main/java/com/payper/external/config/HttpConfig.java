package com.payper.external.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpConfig {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(java.time.Duration.ofSeconds(15))
                .readTimeout(java.time.Duration.ofSeconds(90))
                .writeTimeout(java.time.Duration.ofSeconds(60))
                .callTimeout(java.time.Duration.ofSeconds(120))
                .retryOnConnectionFailure(true)
                // .protocols(java.util.List.of(Protocol.HTTP_1_1)) // 프록시/방화벽 문제 의심되면 활성화
                .connectionPool(new ConnectionPool(5, 2, java.util.concurrent.TimeUnit.MINUTES))
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}

