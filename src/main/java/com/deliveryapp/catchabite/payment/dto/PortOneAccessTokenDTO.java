package com.deliveryapp.catchabite.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * PortOneAccessTokenDTO: PortOne API 인증 토큰 응답
 * 
 * Description: PortOne /users/getToken 엔드포인트에서 받는 응답을 매핑합니다.
 * API 호출 시 필요한 액세스 토큰을 얻기 위해 사용됩니다.
 * 
 * Required Variables/Parameters:
 * - code (int): 응답 코드 (0 = 성공)
 * - message (String): 응답 메시지
 * - response (AccessTokenResponse): 토큰 정보
 * 
 * Output/Data Flow:
 * - Receives from PortOne /users/getToken
 * - Stored temporarily in PortOneService
 * 
 * Dependencies: Lombok, Jackson
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortOneAccessTokenDTO {
    
    @JsonProperty("code")
    private int code;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("response")
    private AccessTokenResponse response;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccessTokenResponse {
        
        /**
         * 액세스 토큰 (API 호출 시 Authorization 헤더에 사용)
         */
        @JsonProperty("access_token")
        private String accessToken;
        
        /**
         * 토큰 타입 (일반적으로 "Bearer")
         */
        @JsonProperty("token_type")
        private String tokenType;
        
        /**
         * 토큰 만료 시간 (초 단위)
         */
        @JsonProperty("expires_in")
        private int expiresIn;
    }
}
