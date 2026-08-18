package com.example.bookmark.global.config;

import com.example.bookmark.global.auth.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // JWT를 쓰므로 CSRF 보호 불필요 (쿠키 기반 세션이 아님)
                .csrf(csrf -> csrf.disable())

                // 세션을 만들지 않음 (완전 Stateless, 토큰으로만 인증)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Spring Security 기본 로그인 폼 / Basic Auth 비활성화
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 실제 인가 판단은 JwtAuthenticationFilter가 담당하므로
                // Security 필터 체인 자체는 모든 요청을 통과시킴
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())

                // UsernamePasswordAuthenticationFilter 자리에 우리 필터를 끼워 넣음
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}