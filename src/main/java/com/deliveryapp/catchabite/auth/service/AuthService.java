package com.deliveryapp.catchabite.auth.service;

import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 회원가입/로그인 비즈니스 로직 처리
 * 비밀번호 암호화 및 검증 담당
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    // 회원 DB 접근 레포지토리
    private final AppUserRepository appUserRepository;

    // 비밀번호 암호화 도구
    private final PasswordEncoder passwordEncoder;

    // 회원가입 처리 (비밀번호 암호화 후 저장)
    public AppUser signup(AppUser appUser) {
        String encoded = passwordEncoder.encode(appUser.getAppUserPassword());
        appUser.changePassword(encoded);
        return appUserRepository.save(appUser);
    }

    // 로그인 처리 (ID 조회 + 비밀번호 검증)
    public AppUser login(String loginId, String password) {

        AppUser user = appUserRepository.findByAppUserLoginId(loginId)
            .orElseThrow(() -> new RuntimeException("로그인 실패"));

        if (!passwordEncoder.matches(password, user.getAppUserPassword())) {
            throw new RuntimeException("로그인 실패");
        }

        return user;
    }
}
