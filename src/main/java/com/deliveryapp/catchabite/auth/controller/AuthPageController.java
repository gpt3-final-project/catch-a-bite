package com.deliveryapp.catchabite.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 로그인/회원가입 화면 이동용 컨트롤러
 * View(html, thymeleaf) 반환 담당
 */
@Controller
public class AuthPageController {

    // 로그인 페이지 이동 (/login)
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // templates/login.html
    }

    // 회원가입 페이지 이동 (/signup)
    @GetMapping("/signup")
    public String signupPage() {
        return "signup"; // templates/signup.html
    }
}
