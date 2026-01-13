package com.deliveryapp.catchabite.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;

/**
 * RestTemplateConfig: Spring RestTemplate Bean 설정
 * 
 * Description: HTTP 요청/응답을 처리할 RestTemplate을 빈으로 등록합니다.
 * PortOne API 호출 시 사용됩니다. 타임아웃 설정으로 무한 대기를 방지합니다.
 * 
 * Required Variables/Parameters:
 * - connectTimeout: 연결 타임아웃 (5초)
 * - readTimeout: 읽기 타임아웃 (5초)
 * 
 * Output/Data Flow:
 * - RestTemplate Bean provided to PortOneService
 * 
 * Dependencies: Spring Framework
 */

@Configuration
public class RestTemplateConfig {
    
    /**
     * RestTemplate Bean 정의
     * PortOne API 호출용 HTTP 클라이언트
     * 
     * @param builder RestTemplateBuilder (Spring에 의해 자동 주입)
     * @return 설정된 RestTemplate
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                // 5초 이내에 연결되지 않으면 타임아웃
                .setConnectTimeout(Duration.ofSeconds(5))
                // 5초 이내에 응답이 없으면 타임아웃
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }
}
