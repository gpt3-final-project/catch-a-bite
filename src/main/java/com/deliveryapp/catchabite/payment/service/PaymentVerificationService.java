package com.deliveryapp.catchabite.payment.service;

import com.deliveryapp.catchabite.common.constant.PaymentConstant;
import com.deliveryapp.catchabite.common.exception.PaymentException;
import com.deliveryapp.catchabite.entity.Payment;
import com.deliveryapp.catchabite.entity.StoreOrder;
import com.deliveryapp.catchabite.payment.dto.PortOnePaymentVerificationDTO;
import com.deliveryapp.catchabite.payment.repository.PaymentRepository;
import com.deliveryapp.catchabite.repository.StoreOrderRepository;
import com.deliveryapp.catchabite.transaction.entity.Transaction;
import com.deliveryapp.catchabite.transaction.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * PaymentVerificationService: 결제 검증 서비스
 * 
 * Description: PortOne에서 조회한 결제 정보와 DB 정보를 비교하여 검증합니다.
 * 금액, 상태, 주문 정보 등을 확인하고 결제를 최종 확정합니다.
 * 
 * Required Variables/Parameters:
 * - portOneService (PortOneService): PortOne API 호출용
 * - paymentRepository (PaymentRepository): Payment 테이블 조회/수정
 * - storeOrderRepository (StoreOrderRepository): StoreOrder 테이블 조회/수정
 * - transactionService (TransactionService): 거래 기록 저장
 * 
 * Output/Data Flow:
 * - Receives paymentData from PortOnePaymentVerificationDTO
 * - Verifies against DB Payment and StoreOrder
 * - Updates Payment.paymentStatus to PAID
 * - Updates StoreOrder.orderStatus to CONFIRMED
 * - Sends Transaction record to TransactionService
 * 
 * Dependencies: PortOneService, PaymentRepository, StoreOrderRepository,
 *               TransactionService, PaymentConstant
 */

@Slf4j
@Service
public class PaymentVerificationService {
    
    private final PortOneService portOneService;
    private final PaymentRepository paymentRepository;
    private final StoreOrderRepository storeOrderRepository;
    private final TransactionService transactionService;
    
    /**
     * 생성자 - Dependency Injection
     * 
     * @param portOneService PortOne API 호출 서비스
     * @param paymentRepository Payment 조회/수정용 Repository
     * @param storeOrderRepository StoreOrder 조회/수정용 Repository
     * @param transactionService 거래 기록 저장용 서비스
     */
    public PaymentVerificationService(
            PortOneService portOneService,
            PaymentRepository paymentRepository,
            StoreOrderRepository storeOrderRepository,
            TransactionService transactionService) {
        this.portOneService = portOneService;
        this.paymentRepository = paymentRepository;
        this.storeOrderRepository = storeOrderRepository;
        this.transactionService = transactionService;
    }
    
    /**
     * 결제 검증 및 최종 확정
     * PortOne에서 조회한 결제 정보를 검증하고, DB를 업데이트합니다.
     * 
     * @param impUid PortOne 결제 고유 ID
     * @param merchantUid 상점 주문 번호
     * @return 검증 완료된 Payment 객체
     * @throws PaymentException 검증 실패 시
     */
    @Transactional
    public Payment verifyAndCompletePayment(String impUid, String merchantUid) {
        
        try {
            log.info("Starting payment verification. imp_uid: {}, merchant_uid: {}", 
                    impUid, merchantUid);
            
            // Step 1: PortOne에서 결제 정보 조회
            PortOnePaymentVerificationDTO portOnePayment = 
                    portOneService.getPaymentDetails(impUid);
            
            PortOnePaymentVerificationDTO.PaymentData paymentData = 
                    portOnePayment.getResponse();
            
            if (paymentData == null) {
                throw new PaymentException(
                        "INVALID_PORTONE_RESPONSE",
                        "PortOne payment data is null"
                );
            }
            
            // Step 2: 기본 검증 - PortOne 상태 확인
            if (!"paid".equals(paymentData.getStatus())) {
                throw new PaymentException(
                        "PAYMENT_NOT_PAID",
                        "PortOne payment status is not 'paid'. Status: " + 
                        paymentData.getStatus()
                );
            }
            
            // Step 3: DB에서 주문 및 기존 결제 정보 조회
            // merchantUid 형식: "ORDER_" + orderId + "_" + timestamp
            Long orderId = extractOrderIdFromMerchantUid(merchantUid);
            
            StoreOrder order = storeOrderRepository.findById(orderId)
                    .orElseThrow(() -> new PaymentException(
                            "ORDER_NOT_FOUND",
                            PaymentConstant.ERROR_ORDER_NOT_FOUND
                    ));
            
            Payment existingPayment = paymentRepository.findByStoreOrder(order)
                    .orElseThrow(() -> new PaymentException(
                            "PAYMENT_NOT_FOUND",
                            PaymentConstant.ERROR_PAYMENT_NOT_FOUND
                    ));
            
            // Step 4: 금액 검증 (매우 중요!)
            Long portOneAmount = paymentData.getAmount();
            Long dbAmount = existingPayment.getPaymentAmount();
            
            if (!portOneAmount.equals(dbAmount)) {
                throw new PaymentException(
                        "AMOUNT_MISMATCH",
                        PaymentConstant.ERROR_AMOUNT_MISMATCH + 
                        " PortOne: " + portOneAmount + ", DB: " + dbAmount
                );
            }
            
            // Step 5: merchantUid 검증
            if (!merchantUid.equals(paymentData.getMerchantUid())) {
                throw new PaymentException(
                        "MERCHANT_UID_MISMATCH",
                        "Merchant UID mismatch. Expected: " + merchantUid + 
                        ", Got: " + paymentData.getMerchantUid()
                );
            }
            
            // Step 6: 이미 결제 완료된 것은 아닌지 확인
            if (PaymentConstant.PAYMENT_STATUS_PAID.equals(
                    existingPayment.getPaymentStatus())) {
                throw new PaymentException(
                        "PAYMENT_ALREADY_PAID",
                        PaymentConstant.ERROR_PAYMENT_ALREADY_COMPLETED
                );
            }
            
            // Step 7: Payment 엔티티 업데이트
            existingPayment.setPaymentStatus(PaymentConstant.PAYMENT_STATUS_PAID);
            existingPayment.setPaymentMethod(paymentData.getPayMethod());
            
            // Unix timestamp를 LocalDateTime으로 변환
            if (paymentData.getPaidAt() != null) {
                LocalDateTime paidAt = Instant.ofEpochSecond(paymentData.getPaidAt())
                        .atZone(ZoneId.of("Asia/Seoul"))
                        .toLocalDateTime();
                existingPayment.setPaymentPaidAt(paidAt);
            }
            
            Payment savedPayment = paymentRepository.save(existingPayment);
            log.info("Payment status updated to PAID. payment_id: {}", 
                    savedPayment.getPaymentId());
            
            // Step 8: StoreOrder 상태 업데이트
            order.setOrderStatus(PaymentConstant.ORDER_STATUS_CONFIRMED);
            storeOrderRepository.save(order);
            log.info("Order status updated to CONFIRMED. order_id: {}", 
                    order.getOrderId());
            
            // Step 9: Transaction 기록 저장
            Transaction transaction = Transaction.builder()
                    .transactionType(com.deliveryapp.catchabite.transaction.entity.TransactionType.USER_PAYMENT)
                    .relatedEntityId(orderId)
                    .relatedEntityType("ORDER")
                    .amount(portOneAmount)
                    .currency("KRW")
                    .transactionStatus(PaymentConstant.TRANSACTION_STATUS_COMPLETED)
                    .portonePaymentId(impUid)
                    .createdAt(LocalDateTime.now())
                    .completedAt(LocalDateTime.now())
                    .build();
            
            transactionService.saveTransaction(transaction);
            log.info("Transaction record saved. transaction_id for order: {}", 
                    orderId);
            
            log.info("Payment verification completed successfully");
            return savedPayment;
            
        } catch (PaymentException pe) {
            log.error("Payment verification failed: {}", pe.getErrorMessage());
            throw pe;
        } catch (Exception e) {
            log.error("Unexpected error during payment verification", e);
            throw new PaymentException(
                    "VERIFICATION_ERROR",
                    "Unexpected error during payment verification",
                    e
            );
        }
    }
    
    /**
     * merchantUid에서 orderId 추출
     * merchantUid 형식: "ORDER_" + orderId + "_" + timestamp
     * 예: "ORDER_123_1705085400000"
     * 
     * @param merchantUid 상점 주문 번호
     * @return 추출된 orderId
     * @throws PaymentException 형식이 잘못된 경우
     */
    private Long extractOrderIdFromMerchantUid(String merchantUid) {
        try {
            String[] parts = merchantUid.split("_");
            if (parts.length < 2) {
                throw new PaymentException(
                        "INVALID_MERCHANT_UID_FORMAT",
                        PaymentConstant.ERROR_INVALID_MERCHANT_UID
                );
            }
            return Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            throw new PaymentException(
                    "INVALID_MERCHANT_UID_FORMAT",
                    PaymentConstant.ERROR_INVALID_MERCHANT_UID,
                    e
            );
        }
    }
    
    /**
     * 결제 실패 처리
     * 결제 실패 시 Payment 상태를 FAILED로 업데이트합니다.
     * 
     * @param merchantUid 상점 주문 번호
     * @param failReason 실패 사유
     * @param failCode PortOne 실패 코드
     * @return 업데이트된 Payment 객체
     */
    @Transactional
    public Payment handlePaymentFailure(String merchantUid, 
                                       String failReason,
                                       String failCode) {
        try {
            log.info("Handling payment failure. merchant_uid: {}, fail_reason: {}", 
                    merchantUid, failReason);
            
            Long orderId = extractOrderIdFromMerchantUid(merchantUid);
            
            StoreOrder order = storeOrderRepository.findById(orderId)
                    .orElseThrow(() -> new PaymentException(
                            "ORDER_NOT_FOUND",
                            PaymentConstant.ERROR_ORDER_NOT_FOUND
                    ));
            
            Payment payment = paymentRepository.findByStoreOrder(order)
                    .orElseThrow(() -> new PaymentException(
                            "PAYMENT_NOT_FOUND",
                            PaymentConstant.ERROR_PAYMENT_NOT_FOUND
                    ));
            
            // 결제 상태 FAILED로 업데이트
            payment.setPaymentStatus(PaymentConstant.PAYMENT_STATUS_FAILED);
            
            Payment savedPayment = paymentRepository.save(payment);
            
            // Transaction 기록 저장 (실패 상태로)
            Transaction failedTransaction = Transaction.builder()
                    .transactionType(com.deliveryapp.catchabite.transaction.entity.TransactionType.USER_PAYMENT)
                    .relatedEntityId(orderId)
                    .relatedEntityType("ORDER")
                    .amount(payment.getPaymentAmount())
                    .currency("KRW")
                    .transactionStatus(PaymentConstant.TRANSACTION_STATUS_FAILED)
                    .failureReason(failReason + " (Code: " + failCode + ")")
                    .createdAt(LocalDateTime.now())
                    .build();
            
            transactionService.saveTransaction(failedTransaction);
            
            log.info("Payment failure handled successfully");
            return savedPayment;
            
        } catch (Exception e) {
            log.error("Error handling payment failure", e);
            throw new PaymentException(
                    "FAILURE_HANDLING_ERROR",
                    "Error handling payment failure",
                    e
            );
        }
    }
}
