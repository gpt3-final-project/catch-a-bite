package com.deliveryapp.catchabite.domain.enumtype;

/**
 * TransactionType: 거래 유형 열거형
 * 
 * 설명: PortOne을 통한 모든 거래의 유형을 정의한다.
 * 각 유형은 다른 비즈니스 로직과 검증을 거친다.
 * 
 * 거래 유형:
 * - USER_PAYMENT: 고객이 음식값을 결제 (카드 → 우리 계좌)
 * - STORE_PAYOUT: 플랫폼이 가맹점에 정산금 지급 (우리 계좌 → 가맹점 계좌)
 * - DELIVERY_PAYOUT: 플랫폼이 라이더에 급여 지급 (우리 계좌 → 라이더 계좌)
 * 
 * 의존성: 없음
 */
public enum TransactionType {
    
    /**
     * 고객 결제: 사용자가 음식값을 결제할 때
     * 흐름: 고객 카드 → Catch-a-Bite 계좌
     * 상태 흐름: PENDING → COMPLETED / FAILED
     */
    USER_PAYMENT("사용자 결제"),
    
    /**
     * 가맹점 정산: Catch-a-Bite가 가맹점에 정산금을 지급할 때
     * 흐름: Catch-a-Bite 계좌 → 가맹점 계좌
     * 상태 흐름: PENDING → COMPLETED / FAILED
     */
    STORE_PAYOUT("사업자 정산"),
    
    /**
     * 라이더 급여: Catch-a-Bite가 라이더에 배달료를 지급할 때
     * 흐름: Catch-a-Bite 계좌 → 라이더 계좌
     * 상태 흐름: PENDING → COMPLETED / FAILED
     */
    DELIVERY_PAYOUT("라이더 급여");
    
    private final String description;
    
    TransactionType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
