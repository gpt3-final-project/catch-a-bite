package com.deliveryapp.catchabite.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * PortOnePaymentRequestDTO: 프론트엔드로부터 받는 결제 요청 DTO
 * 
 * Description: React Native 클라이언트가 결제 준비 요청 시 보내는 데이터입니다.
 * 주문 ID, 금액, 구매자 정보를 포함합니다.
 * 
 * Required Variables/Parameters:
 * - orderId (Long): 주문 ID (StoreOrder.orderId)
 * - paymentAmount (Long): 결제 금액 (단위: KRW)
 * - paymentMethod (String): 결제 방법 (card, transfer, vbank 등)
 * - buyerName (String): 구매자 이름
 * - buyerEmail (String): 구매자 이메일
 * - buyerTel (String): 구매자 전화번호
 * 
 * Output/Data Flow:
 * - Receives from React Native /api/payments/prepare endpoint
 * - Sends to PaymentService for processing
 * 
 * Dependencies: Lombok, Jackson
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortOnePaymentRequestDTO {
    
    /**
     * 주문 ID - StoreOrder.orderId와 매핑
     */
    @JsonProperty("order_id")
    private Long orderId;
    
    /**
     * 결제 금액 (단위: KRW)
     * 예: 25000 = 25,000원
     */
    @JsonProperty("payment_amount")
    private Long paymentAmount;
    
    /**
     * 결제 방법
     * 가능한 값: card, transfer, vbank
     */
    @JsonProperty("payment_method")
    private String paymentMethod;
    
    /**
     * 구매자 이름 (AppUser.appUserName)
     */
    @JsonProperty("buyer_name")
    private String buyerName;
    
    /**
     * 구매자 이메일 (AppUser.appUserEmail)
     */
    @JsonProperty("buyer_email")
    private String buyerEmail;
    
    /**
     * 구매자 전화번호 (AppUser.appUserMobile)
     */
    @JsonProperty("buyer_tel")
    private String buyerTel;
    
    /**
     * 구매자 주소 (선택사항)
     */
    @JsonProperty("buyer_addr")
    private String buyerAddr;
    
    /**
     * 상품명 (선택사항, 예: "Catch-a-Bite 주문")
     */
    @JsonProperty("name")
    private String name;
}
