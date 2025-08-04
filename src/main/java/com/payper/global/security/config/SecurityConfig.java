package com.payper.global.security.config;

import com.payper.global.security.filter.AuthenticationErrorFilter;
import com.payper.global.security.filter.JwtAuthenticationFilter;
import com.payper.global.security.handler.CustomAccessDeniedHandler;
import com.payper.global.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.List;

@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
@ComponentScan(basePackages = {
        "com.payper.global.security",
        "com.payper.domain"
})
//@MapperScan(basePackages={“userdetails 갖고 올때 사용할 매퍼 경로”})
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final AuthenticationErrorFilter authenticationErrorFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    //@Autowired
    //private JwtUsernamePasswordAuthenticationFilter jwtUsernamePasswordAuthenticationFilter;

    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;


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
                // 필터 순서 설정
                .addFilterBefore(encodingFilter(), CsrfFilter.class) // 인코딩 필터 배치. 필터 순서지정.
                .addFilterBefore(authenticationErrorFilter, UsernamePasswordAuthenticationFilter.class) // 인증 에러 필터
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // jwt 인증 필터 앞에다 배치

                // CORS 설정 - SecurityFilterChain 내에서 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 기본 인증 설정 비활성화
                .httpBasic(httpBasic -> httpBasic.disable()) // 기본 http인증 비활성화
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .formLogin(formLogin -> formLogin.disable()) // form기반 로그인 비활성화 == 관련 필터 해제

                // 모든 요청은 인증된 사용자만 접근 가능
                //.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())

//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/favicon.ico",
//                                "/error",
//                                "/test/**",
//                                "/docs/**",
//                                "/api/auth/**",
//                                "/ws/**"
//                        ).permitAll()
//                        .anyRequest().authenticated()
//                )

                // 세션 관리 설정
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 생성모드 설정.

                // 예외 처리 설정
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(authenticationEntryPoint) // 토큰 예외 말고 인증/인가 관련 에러 처리하는 커스텀 핸들러들 등록.
                                .accessDeniedHandler(accessDeniedHandler)

                );

        return http.build();
    }

    @Bean
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web)->{
            web.ignoring().requestMatchers("/assets/**", "/api/auth/**");
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean // CORS 설정을 위한 CorsConfigurationSource
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://payper-client.vercel.app"
        ));
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
