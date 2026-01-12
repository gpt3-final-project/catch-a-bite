package com.deliveryapp.catchabite.auth.security;

import com.deliveryapp.catchabite.common.constant.RoleConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

/**
 * 로그인 성공 후 역할(Role)별 화면 분기 처리
 * 권한에 따라 메인 페이지 리다이렉트
 */
public class RoleBasedLoginSuccessHandler implements AuthenticationSuccessHandler {

    // 로그인 성공 시 호출
    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {

        // 사용자 권한(Role) 확인
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String roleName = authority.getAuthority();

            // 일반 사용자 메인 이동
            if (RoleConstant.ROLE_USER.equals(roleName)) {
                response.sendRedirect("/user/main");
                return;
            }

            // 사장님 메인 이동
            if (RoleConstant.ROLE_OWNER.equals(roleName)) {
                response.sendRedirect("/owner/main");
                return;
            }

            // 배달원 메인 이동
            if (RoleConstant.ROLE_RIDER.equals(roleName)) {
                response.sendRedirect("/rider/main");
                return;
            }
        }

        // 기본 페이지 이동
        response.sendRedirect("/");
    }
}
