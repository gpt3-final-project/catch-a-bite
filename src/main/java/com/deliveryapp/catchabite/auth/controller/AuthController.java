package com.deliveryapp.catchabite.auth.controller;

import com.deliveryapp.catchabite.auth.service.AuthService;
import com.deliveryapp.catchabite.entity.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 인증(회원가입/로그인) API 컨트롤러
 * 요청을 받아 AuthService로 전달
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    // 회원가입/로그인 비즈니스 로직 담당
    private final AuthService authService;

    // 회원가입 요청 처리 (/auth/signup)
    @PostMapping("/signup")
    public AppUser signup(@RequestBody AppUser appUser) {
        return authService.signup(appUser);
    }

    // 로그인 요청 처리 (/auth/login)
    @PostMapping("/login")
    public AppUser login(
        @RequestParam String loginId,   // 로그인 ID
        @RequestParam String password   // 로그인 비밀번호
    ) {
        return authService.login(loginId, password);
    }
}
