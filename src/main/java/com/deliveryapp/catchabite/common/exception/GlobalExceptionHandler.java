package com.deliveryapp.catchabite.common.exception;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 컨트롤러
 * 예외를 공통 ApiResponse로 변환
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 예외 처리
    @ExceptionHandler(AppException.class)
    public ApiResponse<Void> handleAppException(AppException e) {
        return ApiResponse.fail(e.getErrorCode().getCode(), e.getMessage());
    }

    // Validation 오류 처리
    @ExceptionHandler(BindException.class)
    public ApiResponse<Void> handleBindException(BindException e) {
        String message = (e.getBindingResult().getFieldError() != null)
            ? e.getBindingResult().getFieldError().getDefaultMessage()
            : ErrorCode.INVALID_REQUEST.getMessage();
        return ApiResponse.fail(ErrorCode.INVALID_REQUEST.getCode(), message);
    }

    // 기타 예외 처리
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {
        return ApiResponse.fail("INTERNAL_ERROR", e.getMessage());
    }
}
