package com.deliveryapp.catchabite.common.response;

/**
 * 공통 API 응답 포맷
 *
 * - success: 성공 여부
 * - data: 실제 데이터
 * - message: 사용자/클라이언트 표시용 메시지(선택)
 */
public record ApiResponse<T>(boolean success, T data, String message) {

	public static <T> ApiResponse<T> ok(T data) {
		return new ApiResponse<>(true, data, null);
	}

	public static <T> ApiResponse<T> ok(T data, String message) {
		return new ApiResponse<>(true, data, message);
	}

	public static <T> ApiResponse<T> fail(String message) {
		return new ApiResponse<>(false, null, message);
	}

	public static <T> ApiResponse<T> fail(String message, T data) {
		return new ApiResponse<>(false, data, message);
	}
}
