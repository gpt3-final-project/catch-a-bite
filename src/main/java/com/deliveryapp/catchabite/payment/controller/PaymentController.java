package com.deliveryapp.catchabite.payment.controller;

import com.deliveryapp.catchabite.common.exception.PaymentException;
import com.deliveryapp.catchabite.entity.Payment;
import com.deliveryapp.catchabite.payment.dto.PortOnePaymentRequestDTO;
import com.deliveryapp.catchabite.payment.dto.PortOnePaymentResponseDTO;
import com.deliveryapp.catchabite.payment.service.PaymentService;
import com.deliveryapp.catchabite.payment.service.PaymentVerificationService;
import com.deliveryapp.catchabite.payment.dto.PaymentCompleteResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * PaymentController: 결제 관련 HTTP 엔드포인트
 * 
 * Description: 프론트엔드의 결제 요청을 처리하고 응답합니다.
 * 결제 준비, 완료 검증, 결과 조회 등의 엔드포인트를 제공합니다.
 * 
 * Required Variables/Parameters:
 * - paymentService (PaymentService): 결제 준비 로직
 * - paymentVerificationService (PaymentVerificationService): 결제 검증 로직
 * 
 * Output/Data Flow:
 * - Receives PortOnePaymentRequestDTO from React Native
 * - Calls PaymentService for preparation
 * - Returns PortOnePaymentResponseDTO to frontend
 * - Receives payment verification request with imp_uid
 * - Calls PaymentVerificationService
 * - Returns PaymentCompleteResponseDTO
 * 
 * Dependencies: PaymentService, PaymentVerificationService, Slf4j
 */

@Slf4j
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    
    private final PaymentService paymentService;
    private final PaymentVerificationService paymentVerificationService;
    
    /**
     * 생성자 - Dependency Injection
     * 
     * @param paymentService 결제 준비 서비스
     * @param paymentVerificationService 결제 검증 서비스
     */
    public PaymentController(PaymentService paymentService,
                            PaymentVerificationService paymentVerificationService) {
        this.paymentService = paymentService;
        this.paymentVerificationService = paymentVerificationService;
    }
    
    /**
     * 1️⃣ 결제 준비 엔드포인트
     * POST /api/payments/prepare
     * 
     * 프론트엔드가 결제 창을 띄우기 전에 호출합니다.
     * Payment 엔티티를 생성하고, PortOne 결제 창에 필요한 정보를 반환합니다.
     * 
     * Request Example:
     * {
     *   "order_id": 123,
     *   "payment_amount": 25000,
     *   "payment_method": "card",
     *   "buyer_name": "김철수",
     *   "buyer_email": "user@example.com",
     *   "buyer_tel": "01012345678",
     *   "buyer_addr": "서울시 강남구",
     *   "name": "Catch-a-Bite 주문"
     * }
     * 
     * Response Example (200 OK):
     * {
     *   "merchant_uid": "ORDER_123_1705085400000",
     *   "imp_key": "imp_1234567890",
     *   "payment_amount": 25000,
     *   "buyer_name": "김철수",
     *   "buyer_email": "user@example.com",
     *   "buyer_tel": "01012345678",
     *   "api_endpoint": "https://api.iamport.kr",
     *   "prepared_at": 1705085400000
     * }
     * 
     * @param request 프론트에서 보낸 결제 요청 정보
     * @return PortOne 결제 창에 필요한 정보와 merchant_uid
     */
    @PostMapping("/prepare")
    public ResponseEntity<?> preparePayment(
            @RequestBody PortOnePaymentRequestDTO request) {
        
        try {
            log.info("Payment prepare request received. order_id: {}", 
                    request.getOrderId());
            
            // 입력 검증
            if (request.getOrderId() == null || request.getOrderId() <= 0) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("INVALID_ORDER_ID", 
                                "Invalid order ID"));
            }
            
            if (request.getPaymentAmount() == null || 
                request.getPaymentAmount() <= 0) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("INVALID_AMOUNT", 
                                "Invalid payment amount"));
            }
            
            // 결제 준비
            PortOnePaymentResponseDTO response = 
                    paymentService.preparePayment(request);
            
            log.info("Payment preparation successful. merchant_uid: {}", 
                    response.getMerchantUid());
            
            return ResponseEntity.ok(response);
            
        } catch (PaymentException pe) {
            log.error("Payment preparation failed: {}", pe.getErrorMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(pe.getErrorCode(), 
                            pe.getErrorMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during payment preparation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("INTERNAL_ERROR", 
                            "Internal server error"));
        }
    }
    
    /**
     * 2️⃣ 결제 완료 검증 엔드포인트
     * POST /api/payments/complete
     * 
     * 프론트엔드가 PortOne에서 결제를 완료한 후 호출합니다.
     * PortOne에서 조회한 실제 결제 정보를 검증하고, Payment/StoreOrder를 업데이트합니다.
     * 
     * Request Example:
     * {
     *   "imp_uid": "imp_1234567890",
     *   "merchant_uid": "ORDER_123_1705085400000"
     * }
     * 
     * Response Example (200 OK - 결제 성공):
     * {
     *   "success": true,
     *   "message": "결제가 성공적으로 완료되었습니다.",
     *   "payment_id": 456,
     *   "order_id": 123,
     *   "imp_uid": "imp_1234567890",
     *   "payment_status": "PAID",
     *   "payment_amount": 25000,
     *   "payment_method": "card",
     *   "payment_paid_at": "2024-01-13T10:30:00"
     * }
     * 
     * Response Example (400 Bad Request - 검증 실패):
     * {
     *   "success": false,
     *   "message": "결제가 실패했습니다.",
     *   "error_code": "AMOUNT_MISMATCH",
     *   "error_message": "결제 금액이 주문 금액과 맞지 않습니다."
     * }
     * 
     * @param impUid PortOne 결제 고유 ID
     * @param merchantUid 상점 주문 번호
     * @return 결제 검증 결과
     */
    @PostMapping("/complete")
    public ResponseEntity<?> completePayment(
            @RequestParam String impUid,
            @RequestParam String merchantUid) {
        
        try {
            log.info("Payment completion request received. imp_uid: {}, merchant_uid: {}", 
                    impUid, merchantUid);
            
            // 입력 검증
            if (impUid == null || impUid.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("MISSING_IMP_UID", 
                                "imp_uid is required"));
            }
            
            if (merchantUid == null || merchantUid.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("MISSING_MERCHANT_UID", 
                                "merchant_uid is required"));
            }
            
            // 결제 검증 및 완료
            Payment payment = paymentVerificationService
                    .verifyAndCompletePayment(impUid, merchantUid);
            
            log.info("Payment completed successfully. payment_id: {}", 
                    payment.getPaymentId());
            
            // 성공 응답 생성
            PaymentCompleteResponseDTO response = 
                    PaymentCompleteResponseDTO.builder()
                    .success(true)
                    .message("결제가 성공적으로 완료되었습니다.")
                    .paymentId(payment.getPaymentId())
                    .orderId(payment.getStoreOrder().getOrderId())
                    .impUid(impUid)
                    .paymentStatus(payment.getPaymentStatus())
                    .paymentAmount(payment.getPaymentAmount())
                    .paymentMethod(payment.getPaymentMethod())
                    .paymentPaidAt(payment.getPaymentPaidAt())
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (PaymentException pe) {
            log.error("Payment verification failed: {}", pe.getErrorMessage());
            
            // 검증 실패 응답
            PaymentCompleteResponseDTO errorResponse = 
                    PaymentCompleteResponseDTO.builder()
                    .success(false)
                    .message("결제 검증에 실패했습니다.")
                    .errorCode(pe.getErrorCode())
                    .errorMessage(pe.getErrorMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errorResponse);
        } catch (Exception e) {
            log.error("Unexpected error during payment completion", e);
            
            PaymentCompleteResponseDTO errorResponse = 
                    PaymentCompleteResponseDTO.builder()
                    .success(false)
                    .message("결제 처리 중 오류가 발생했습니다.")
                    .errorCode("INTERNAL_ERROR")
                    .errorMessage("Internal server error")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
    
    /**
     * 3️⃣ 결제 정보 조회 엔드포인트 (선택사항)
     * GET /api/payments/{paymentId}
     * 
     * 특정 결제의 상세 정보를 조회합니다.
     * 
     * Response Example (200 OK):
     * {
     *   "payment_id": 456,
     *   "order_id": 123,
     *   "payment_method": "card",
     *   "payment_amount": 25000,
     *   "payment_status": "PAID",
     *   "payment_paid_at": "2024-01-13T10:30:00"
     * }
     * 
     * @param paymentId 결제 ID
     * @return 결제 정보
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentDetails(@PathVariable Long paymentId) {
        try {
            log.info("Payment details request received. payment_id: {}", paymentId);
            
            Payment payment = paymentService.getPaymentById(paymentId);
            
            PaymentCompleteResponseDTO response = 
                    PaymentCompleteResponseDTO.builder()
                    .success(true)
                    .message("결제 정보를 조회했습니다.")
                    .paymentId(payment.getPaymentId())
                    .orderId(payment.getStoreOrder().getOrderId())
                    .paymentStatus(payment.getPaymentStatus())
                    .paymentAmount(payment.getPaymentAmount())
                    .paymentMethod(payment.getPaymentMethod())
                    .paymentPaidAt(payment.getPaymentPaidAt())
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (PaymentException pe) {
            log.error("Payment details fetch failed: {}", pe.getErrorMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(pe.getErrorCode(), 
                            pe.getErrorMessage()));
        }
    }
    
    /**
     * 에러 응답 객체 생성 헬퍼 메서드
     * 
     * @param errorCode 에러 코드
     * @param errorMessage 에러 메시지
     * @return 에러 응답 객체
     */
    private PaymentCompleteResponseDTO createErrorResponse(String errorCode, 
                                                           String errorMessage) {
        return PaymentCompleteResponseDTO.builder()
                .success(false)
                .message("오류가 발생했습니다.")
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }
}
