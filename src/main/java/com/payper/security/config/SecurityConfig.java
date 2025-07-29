package com.payper.security.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@Log4j2
//@ComponentScan(basePackages={“쓸 컴포넌트경로”})
//@MapperScan(basePackages={“userdetails 갖고 올때 사용할 매퍼 경로”})
public class SecurityConfig {
    public CharacterEncodingFilter encodingFilter( ){
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter( );
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        return encodingFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder( ){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(encodingFilter(), CsrfFilter.class)
                .csrf().disable()
//                .csrf(Customizer.withDefaults())              // CSRF 보호 기본 설정 적용 (생략해도 자동 적용됨)
                //.httpBasic(Customizer.withDefaults())         // HTTP 기본 인증 사용 (브라우저 팝업 뜨는 방식)
                //.formLogin(Customizer.withDefaults())         // 기본 로그인 폼 사용
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()             // 모든 요청은 인증된 사용자만 접근 가능
                );
        return http.build();
    }
}