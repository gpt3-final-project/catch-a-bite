package com.deliveryapp.catchabite.auth.domain;

/**
 * 계정 상태 구분 Enum
 * 회원 사용 가능 여부 판단용
 */
public enum AccountStatus {

    ACTIVE,      // 정상 사용 계정
    SUSPENDED,   // 이용 정지 계정
    WITHDRAWN   // 탈퇴 계정
}
