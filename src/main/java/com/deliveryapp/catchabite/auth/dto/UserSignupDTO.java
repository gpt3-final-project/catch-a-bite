package com.deliveryapp.catchabite.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 일반 사용자 회원가입 요청 DTO
 * 회원 기본 정보 입력값 검증용
 */
public class UserSignupDTO {

    // 사용자 이름 (최대 100자)
    @NotBlank
    @Size(max = 100)
    private String name;

    // 로그인 ID (이메일)
    @NotBlank
    private String loginId;

    // 로그인 비밀번호 (8자 이상)
    @NotBlank
    @Size(min = 8)
    private String password;

    // 사용자 닉네임 (최대 50자)
    @NotBlank
    @Size(max = 50)
    private String nickname;

    // 휴대폰 번호 (숫자 11자리)
    @NotBlank
    @Pattern(regexp = "^[0-9]{11}$")
    private String mobile;

    // name 반환
    public String getName() {
        return name;
    }

    // name 설정
    public void setName(String name) {
        this.name = name;
    }

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

    // nickname 반환
    public String getNickname() {
        return nickname;
    }

    // nickname 설정
    public void setNickname(String nickname) {
        this.nickname = nickname;
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
