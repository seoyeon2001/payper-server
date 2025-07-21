package com.payper.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@Log4j2
//@ComponentScan(basePackages={“쓸 컴포넌트경로”})
//@MapperScan(basePackages={“userdetails 갖고 올때 사용할 매퍼 경로”})
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
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

    @Override
    public void configure(HttpSecurity http) throws Exception{
        http.addFilterBefore(encodingFilter(), CsrfFilter.class);

        http.authorizeRequests( )
                .antMatchers("/").permitAll();
        //SecurityConfig에 언급되지 않은 url들은 기본적으로 security filter chain을 거친다.
    }
}