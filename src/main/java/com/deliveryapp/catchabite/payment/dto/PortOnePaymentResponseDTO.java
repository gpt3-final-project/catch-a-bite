package com.deliveryapp.catchabite.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * PortOnePaymentResponseDTO: 결제 준비 후 프론트엔드에 보내는 응답
 * 
 * Description: 결제 준비가 완료되면 백엔드가 이 DTO로 응답합니다.
 * PortOne의 결제 창 호출에 필요한 데이터를 포함합니다.
 * 
 * Required Variables/Parameters:
 * - merchantUid (String): 상점 주문 번호 (고유해야 함)
 * - impKey (String): PortOne 상점 API Key
 * - paymentAmount (Long): 결제 금액
 * - buyerName (String): 구매자 이름
 * - buyerEmail (String): 구매자 이메일
 * 
 * Output/Data Flow:
 * - Sent to React Native from /api/payments/prepare endpoint
 * - Used by frontend to call IMP.request_pay()
 * 
 * Dependencies: Lombok, Jackson
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortOnePaymentResponseDTO {
    
    /**
     * 상점 주문 번호 (merchant_uid)
     * 형식: "ORDER_" + orderId + "_" + timestamp
     */
    @JsonProperty("merchant_uid")
    private String merchantUid;
    
    /**
     * PortOne 상점 API Key
     * 프론트에서 IMP.request_pay() 호출 시 필요
     */
    @JsonProperty("imp_key")
    private String impKey;
    
    /**
     * 결제 금액 (단위: KRW)
     */
    @JsonProperty("payment_amount")
    private Long paymentAmount;
    
    /**
     * 구매자 이름
     */
    @JsonProperty("buyer_name")
    private String buyerName;
    
    /**
     * 구매자 이메일
     */
    @JsonProperty("buyer_email")
    private String buyerEmail;
    
    /**
     * 구매자 전화번호
     */
    @JsonProperty("buyer_tel")
    private String buyerTel;
    
    /**
     * PortOne API 엔드포인트 URL
     * (프론트에서 참고용)
     */
    @JsonProperty("api_endpoint")
    private String apiEndpoint;
    
    /**
     * 준비 완료 시간 (타임스탬프)
     */
    @JsonProperty("prepared_at")
    private Long preparedAt;
}
