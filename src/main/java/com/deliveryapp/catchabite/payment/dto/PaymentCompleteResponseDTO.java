package com.deliveryapp.catchabite.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDateTime;

/**
 * PaymentCompleteResponseDTO: 결제 완료 후 프론트엔드에 보내는 최종 응답
 * 
 * Description: 결제 검증 완료 후 최종 결과를 프론트엔드에 반환합니다.
 * 결제 성공/실패 여부와 상세 정보를 포함합니다.
 * 
 * Required Variables/Parameters:
 * - success (boolean): 결제 성공 여부
 * - message (String): 결과 메시지
 * - paymentId (Long): Payment.paymentId
 * - orderId (Long): StoreOrder.orderId
 * 
 * Output/Data Flow:
 * - Sent to React Native from /api/payments/complete endpoint
 * - Used by frontend to show payment result UI
 * 
 * Dependencies: Lombok, Jackson
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCompleteResponseDTO {
    
    /**
     * 결제 성공 여부
     */
    @JsonProperty("success")
    private boolean success;
    
    /**
     * 결과 메시지
     */
    @JsonProperty("message")
    private String message;
    
    /**
     * Payment.paymentId (DB에서 저장된 결제 ID)
     */
    @JsonProperty("payment_id")
    private Long paymentId;
    
    /**
     * StoreOrder.orderId (주문 ID)
     */
    @JsonProperty("order_id")
    private Long orderId;
    
    /**
     * PortOne 결제 고유 ID
     */
    @JsonProperty("imp_uid")
    private String impUid;
    
    /**
     * 결제 상태
     * PAID, FAILED, CANCELLED 등
     */
    @JsonProperty("payment_status")
    private String paymentStatus;
    
    /**
     * 결제 금액 (단위: KRW)
     */
    @JsonProperty("payment_amount")
    private Long paymentAmount;
    
    /**
     * 결제 방법
     */
    @JsonProperty("payment_method")
    private String paymentMethod;
    
    /**
     * 결제 시간
     */
    @JsonProperty("payment_paid_at")
    private LocalDateTime paymentPaidAt;
    
    /**
     * 에러 코드 (실패 시에만)
     */
    @JsonProperty("error_code")
    private String errorCode;
    
    /**
     * 에러 메시지 (실패 시에만)
     */
    @JsonProperty("error_message")
    private String errorMessage;
}
