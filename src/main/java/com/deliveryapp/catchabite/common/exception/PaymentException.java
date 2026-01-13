package com.deliveryapp.catchabite.common.exception;

/**
 * PaymentException: 결제 처리 중 발생하는 커스텀 예외
 * 
 * Description: 결제 시스템에서 발생하는 모든 에러를 처리하는 중앙 예외 클래스입니다.
 * 
 * Required Variables/Parameters:
 * - message (String): 에러 메시지
 * - cause (Throwable): 근본 원인
 * - errorCode (String): 에러 코드
 * 
 * Output/Data Flow: 결제 관련 모든 Service/Controller에서 throw됩니다.
 * 
 * Dependencies: 없음 (standalone exception class)
 */

public class PaymentException extends RuntimeException {
    
    private String errorCode;
    private String message;

    public PaymentException(String message) {
        super(message);
        this.message = message;
    }

    public PaymentException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public PaymentException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return message;
    }
}
