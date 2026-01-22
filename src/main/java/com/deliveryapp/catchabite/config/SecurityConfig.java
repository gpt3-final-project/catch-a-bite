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
            .cors(Customizer.withDefaults())

            // 결제 API는 CSRF 예외 처리
            .csrf(csrf -> csrf
                // API 전체 CSRF 예외 (MockMvc 테스트 시 편의성 고려)
                .ignoringRequestMatchers("/api/**")

                // 결제 API 명시적 예외 (이미 /api/**에 포함되지만 가독성 목적)
                .ignoringRequestMatchers("/api/payments/**")
            )

            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )

            // authorizeHttpRequests 는 반드시 한 번만 선언
            .authorizeHttpRequests(auth -> auth
                // Preflight 요청 허용
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 정적 리소스 / 페이지
                .requestMatchers("/", "/index.html", "/auth/**").permitAll()

                // Auth 엔드포인트 (주의: /api/v1/auth/me 는 permitAll 아님)
                .requestMatchers("/api/v1/auth/login").permitAll()
                .requestMatchers("/api/v1/auth/signup").permitAll()
                .requestMatchers("/api/v1/auth/exists/**").permitAll()
                .requestMatchers("/api/v1/deliverer/auth/**").permitAll()
                .requestMatchers("/api/v1/store-owner/auth/**").permitAll()

                // 결제 API 공개
                .requestMatchers("/api/payments/**").permitAll()

                /*****************************************************************
                 * [추가] 역할별 Delivery Controller 보안 규칙
                 *
                 * - DeliveryController            : /api/deliveries/**
                 * - UserDeliveryController        : /api/user/deliveries/**
                 * - StoreDeliveryController       : /api/store/deliveries/**
                 * - DelivererDeliveryController   : /api/deliverer/deliveries/**
                 *****************************************************************/

                // (A) 고객(USER) - 내 배달 조회
                .requestMatchers(HttpMethod.GET,
                    "/api/user/deliveries",
                    "/api/user/deliveries/*"
                ).hasRole("USER")

                // (B) 점주(STORE_OWNER) - 배달 조회 (내 가게 기준)
                .requestMatchers(HttpMethod.GET,
                    "/api/store/deliveries",
                    "/api/store/deliveries/*",
                    "/api/store/deliveries/status"
                ).hasRole("STORE_OWNER")

                // (C) 라이더(RIDER) - 배달 조회 (내 배달)
                .requestMatchers(HttpMethod.GET,
                    "/api/deliverer/deliveries",
                    "/api/deliverer/deliveries/*",
                    "/api/deliverer/deliveries/status"
                ).hasRole("RIDER")

                // (D) 배달 단건 조회 (공통)
                //     관리자/고객/점주/라이더 모두 접근 가능
                //     예: /api/deliveries/{deliveryId}
                .requestMatchers(HttpMethod.GET,
                    "/api/deliveries/*"
                ).hasAnyRole("USER", "RIDER", "STORE_OWNER")

                // (E) 점주(STORE_OWNER) - 배달 배정 / 재오픈
                .requestMatchers(HttpMethod.POST,
                    "/api/deliveries/*/assign",
                    "/api/deliveries/*/reopen"
                ).hasRole("STORE_OWNER")

                // (F) 라이더(RIDER) - 배달 진행 액션
                .requestMatchers(HttpMethod.POST,
                    "/api/deliveries/*/accept",
                    "/api/deliveries/*/pickup-complete",
                    "/api/deliveries/*/start",
                    "/api/deliveries/*/complete"
                ).hasRole("RIDER")

                // v1 API 규칙 (기존 호환성 유지)
                .requestMatchers(HttpMethod.PATCH, "/api/v1/riders/me/password").hasRole("RIDER")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/owners/me/password").hasRole("STORE_OWNER")
                .requestMatchers("/api/v1/users/**").hasRole("USER")
                .requestMatchers("/api/v1/addresses/**").hasRole("USER")
                .requestMatchers("/api/v1/user/profile").hasRole("USER")
                .requestMatchers("/api/v1/user/**").authenticated()
                .requestMatchers("/api/v1/rider/**").hasRole("RIDER")
                .requestMatchers("/api/v1/owner/**").hasRole("STORE_OWNER")

                // 나머지 v1 API는 인증 필요
                .requestMatchers("/api/v1/**").authenticated()

                // 그 외 요청
                .anyRequest().permitAll()
            )

            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
