package com.deliveryapp.catchabite.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 사장님 회원가입 요청 DTO
 * 회원가입 입력값 검증용
 */
public class OwnerSignupDTO {

    // 로그인 ID (필수)
    @NotBlank(message = "loginId is required")
    private String loginId;

    // 로그인 비밀번호 (필수)
    @NotBlank(message = "password is required")
    private String password;

    // 휴대폰 번호 (숫자 11자리)
    @NotBlank(message = "mobile is required")
    @Pattern(regexp = "^[0-9]{11}$")
    private String mobile;

    // loginId 반환
    public String getLoginId() {
        return loginId;
    }

    // loginId 설정
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    // password 반환
    public String getPassword() {
        return password;
    }

    // password 설정
    public void setPassword(String password) {
        this.password = password;
    }

    // mobile 반환
    public String getMobile() {
        return mobile;
    }

    // mobile 설정
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
