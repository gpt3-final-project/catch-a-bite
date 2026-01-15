package com.deliveryapp.catchabite.config;

import com.deliveryapp.catchabite.security.RestAccessDeniedHandler;
import com.deliveryapp.catchabite.security.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**

 * SecurityConfig: 애플리케이션 전역 Spring Security 정책 설정
 *
 * - JWT 없이 운영/개발 가능한 기본 설정
 * - React 호출을 위해 CORS + OPTIONS(preflight) 허용
 * - /api/v1/auth/** 는 CSRF 예외 처리 (로그인/회원가입 API 편의)
 * - 현재는 전체 permitAll (서버 부팅/연동 안정 우선)
 * Spring Security 설정
 * - 인증 API는 공개, 나머지 API는 세션 로그인 필요
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean

    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        RestAuthenticationEntryPoint authenticationEntryPoint,
        RestAccessDeniedHandler accessDeniedHandler
    ) throws Exception {

        http
            .cors(cors -> {})
            // API 호출 중심이면 CSRF 끄는게 편함
            .csrf(csrf -> csrf.disable())

            // 세션 기반 로그인 유지 (필요 시 세션 생성)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 정적/페이지
                .requestMatchers("/", "/index.html", "/auth/**").permitAll()

                // 개인/공통 인증 API
                .requestMatchers("/api/v1/auth/**").permitAll()

                // 라이더 인증 API
                .requestMatchers("/api/v1/deliverer/auth/**").permitAll()

                // 사장님 인증 API
                .requestMatchers("/api/v1/store-owner/auth/**").permitAll()

                // (있으면) 사업자 인증 API도 permitAll로 추가
                // .requestMatchers("/api/v1/store-owner/auth/**").permitAll()
                                  
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                "/api/payments/**")
                
                .requestMatchers("/api/v1/user/profile").hasRole("USER")
                .requestMatchers("/api/v1/user/**").authenticated()
                .requestMatchers("/api/v1/rider/**").hasRole("RIDER")
                .requestMatchers("/api/v1/owner/**").hasRole("STORE_OWNER")

                // 나머지 API는 로그인 필요
                .requestMatchers("/api/v1/**").authenticated()

                // 그 외는 공개(원하면 authenticated로 변경 가능)
                .anyRequest().permitAll()

                // 보호가 필요해지면 아래처럼 변경
                // .anyRequest().authenticated()
            )

            // ===== 인증 방식 비활성화 =====
            // 폼 로그인 사용 안 함
            // API 방식이므로 기본 폼/베이직 끔
            .formLogin(form -> form.disable())

            // Basic 인증 사용 안 함
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
