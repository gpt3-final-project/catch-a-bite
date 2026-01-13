package com.deliveryapp.catchabite.payment.service;

import com.deliveryapp.catchabite.config.PortOneConfig;
import com.deliveryapp.catchabite.common.constant.PaymentConstant;
import com.deliveryapp.catchabite.common.exception.PaymentException;
import com.deliveryapp.catchabite.payment.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

/**
 * PortOneService: PortOne API와의 통신을 담당하는 서비스
 * 
 * Description: PortOne의 결제 조회, 취소 등의 API를 호출하고 결과를 처리합니다.
 * 토큰 관리, 에러 처리, 데이터 변환을 담당합니다.
 * 
 * Required Variables/Parameters:
 * - portOneConfig (PortOneConfig): PortOne 설정 (store ID, secret key)
 * - restTemplate (RestTemplate): HTTP 클라이언트
 * - objectMapper (ObjectMapper): JSON 변환
 * 
 * Output/Data Flow:
 * - Sends API calls to PortOne /payments, /payments/cancel endpoints
 * - Receives PortOnePaymentVerificationDTO from PortOne
 * - Sends responses to PaymentVerificationService
 * 
 * Dependencies: PortOneConfig, RestTemplate, ObjectMapper, Slf4j
 */

@Slf4j
@Service
public class PortOneService {
    
    private final PortOneConfig portOneConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    // 토큰 캐싱용 (실제 환경에서는 Redis 추천)
    private String cachedAccessToken;
    private long tokenExpiryTime;
    
    /**
     * 생성자 - Dependency Injection
     * 
     * @param portOneConfig PortOne 설정
     * @param restTemplate REST 클라이언트
     * @param objectMapper JSON 변환기
     */
    public PortOneService(PortOneConfig portOneConfig, 
                         RestTemplate restTemplate,
                         ObjectMapper objectMapper) {
        this.portOneConfig = portOneConfig;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    /**
     * PortOne API 인증 토큰 획득
     * POST /users/getToken
     * 
     * @return 액세스 토큰
     * @throws PaymentException PortOne API 호출 실패 시
     */
    public String getAccessToken() {
        // 토큰이 캐시되어 있고 만료되지 않았으면 기존 토큰 반환
        if (cachedAccessToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            log.info("Using cached PortOne access token");
            return cachedAccessToken;
        }
        
        try {
            log.info("Requesting new PortOne access token");
            
            // 요청 바디 생성: store ID와 secret key를 JSON으로
            String requestBody = String.format(
                    "{\"imp_key\":\"%s\",\"imp_secret\":\"%s\"}",
                    portOneConfig.getStoreId(),
                    portOneConfig.getSecretKey()
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.set(PaymentConstant.HEADER_CONTENT_TYPE, 
                       PaymentConstant.CONTENT_TYPE_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            
            // PortOne API 호출
            String url = portOneConfig.getApiUrl() + 
                        PaymentConstant.PORTONE_ACCESS_TOKEN_ENDPOINT;
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            
            // 응답 파싱
            PortOneAccessTokenDTO tokenResponse = 
                    objectMapper.readValue(response.getBody(), 
                                          PortOneAccessTokenDTO.class);
            
            if (tokenResponse.getCode() != 0) {
                throw new PaymentException(
                        "TOKEN_FETCH_FAILED",
                        "Failed to get PortOne access token: " + 
                        tokenResponse.getMessage()
                );
            }
            
            // 토큰 캐싱 (만료 시간 전에 갱신하도록 95% 사용)
            cachedAccessToken = tokenResponse.getResponse().getAccessToken();
            tokenExpiryTime = System.currentTimeMillis() + 
                             (tokenResponse.getResponse().getExpiresIn() * 950);
            
            log.info("Successfully obtained PortOne access token");
            return cachedAccessToken;
            
        } catch (RestClientException e) {
            log.error("RestTemplate error while getting PortOne token", e);
            throw new PaymentException(
                    "PORTONE_API_ERROR",
                    PaymentConstant.ERROR_PORTONE_API_FAILED,
                    e
            );
        } catch (Exception e) {
            log.error("Error while getting PortOne access token", e);
            throw new PaymentException(
                    "TOKEN_PROCESSING_ERROR",
                    "Error processing PortOne token response",
                    e
            );
        }
    }
    
    /**
     * PortOne에서 결제 정보 조회
     * GET /payments/{imp_uid}
     * 
     * @param impUid PortOne 결제 고유 ID
     * @return 결제 정보
     * @throws PaymentException PortOne API 호출 실패 시
     */
    public PortOnePaymentVerificationDTO getPaymentDetails(String impUid) {
        try {
            log.info("Fetching payment details from PortOne. imp_uid: {}", impUid);
            
            String accessToken = getAccessToken();
            
            HttpHeaders headers = new HttpHeaders();
            headers.set(PaymentConstant.HEADER_AUTHORIZATION, 
                       "Bearer " + accessToken);
            headers.set(PaymentConstant.HEADER_CONTENT_TYPE, 
                       PaymentConstant.CONTENT_TYPE_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // PortOne API 호출
            String url = portOneConfig.getApiUrl() + 
                        PaymentConstant.PORTONE_PAYMENT_GET_ENDPOINT
                        .replace("{imp_uid}", impUid);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            
            // 응답 파싱
            PortOnePaymentVerificationDTO paymentDetails = 
                    objectMapper.readValue(response.getBody(), 
                                          PortOnePaymentVerificationDTO.class);
            
            if (paymentDetails.getCode() != 0) {
                throw new PaymentException(
                        "PAYMENT_FETCH_FAILED",
                        "Failed to fetch payment details: " + 
                        paymentDetails.getMessage()
                );
            }
            
            log.info("Successfully fetched payment details from PortOne");
            return paymentDetails;
            
        } catch (PaymentException pe) {
            throw pe;
        } catch (RestClientException e) {
            log.error("RestTemplate error while fetching payment details", e);
            throw new PaymentException(
                    "PORTONE_API_ERROR",
                    PaymentConstant.ERROR_PORTONE_API_FAILED,
                    e
            );
        } catch (Exception e) {
            log.error("Error while fetching payment details", e);
            throw new PaymentException(
                    "PAYMENT_PROCESSING_ERROR",
                    "Error processing PortOne payment response",
                    e
            );
        }
    }
    
    /**
     * PortOne에서 결제 취소
     * POST /payments/cancel
     * 
     * @param impUid PortOne 결제 고유 ID
     * @param cancelReason 취소 사유
     * @return 취소 결과
     * @throws PaymentException PortOne API 호출 실패 시
     */
    public PortOnePaymentVerificationDTO cancelPayment(String impUid, 
                                                        String cancelReason) {
        try {
            log.info("Cancelling payment in PortOne. imp_uid: {}", impUid);
            
            String accessToken = getAccessToken();
            
            // 요청 바디: imp_uid와 취소 사유
            String requestBody = String.format(
                    "{\"imp_uid\":\"%s\",\"reason\":\"%s\"}",
                    impUid,
                    cancelReason != null ? cancelReason : "User requested"
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.set(PaymentConstant.HEADER_AUTHORIZATION, 
                       "Bearer " + accessToken);
            headers.set(PaymentConstant.HEADER_CONTENT_TYPE, 
                       PaymentConstant.CONTENT_TYPE_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            
            // PortOne API 호출
            String url = portOneConfig.getApiUrl() + 
                        PaymentConstant.PORTONE_CANCEL_PAYMENT_ENDPOINT;
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            
            // 응답 파싱
            PortOnePaymentVerificationDTO cancelResult = 
                    objectMapper.readValue(response.getBody(), 
                                          PortOnePaymentVerificationDTO.class);
            
            if (cancelResult.getCode() != 0) {
                throw new PaymentException(
                        "PAYMENT_CANCEL_FAILED",
                        "Failed to cancel payment: " + 
                        cancelResult.getMessage()
                );
            }
            
            log.info("Successfully cancelled payment in PortOne");
            return cancelResult;
            
        } catch (PaymentException pe) {
            throw pe;
        } catch (RestClientException e) {
            log.error("RestTemplate error while cancelling payment", e);
            throw new PaymentException(
                    "PORTONE_API_ERROR",
                    PaymentConstant.ERROR_PORTONE_API_FAILED,
                    e
            );
        } catch (Exception e) {
            log.error("Error while cancelling payment", e);
            throw new PaymentException(
                    "CANCEL_PROCESSING_ERROR",
                    "Error processing PortOne cancel response",
                    e
            );
        }
    }
}
