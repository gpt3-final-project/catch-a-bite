package com.deliveryapp.catchabite.auth.security;

import com.deliveryapp.catchabite.auth.store.InMemoryAccountStore;
import com.deliveryapp.catchabite.auth.store.InMemoryAccountStore.AccountRecord;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Spring Security 인증용 UserDetailsService
 * 메모리 계정 저장소에서 사용자 조회
 */
public class InMemoryUserDetailsService implements UserDetailsService {

    // 메모리 기반 계정 저장소
    private final InMemoryAccountStore inMemoryAccountStore;

    public InMemoryUserDetailsService(InMemoryAccountStore inMemoryAccountStore) {
        this.inMemoryAccountStore = inMemoryAccountStore;
    }

    // 로그인 시 사용자 정보 조회
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // loginId 기준 계정 조회
        AccountRecord accountRecord = inMemoryAccountStore.getAccount(username);

        // 계정 없을 경우 인증 실패
        if (accountRecord == null) {
            throw new UsernameNotFoundException("account not found");
        }

        // Spring Security User 객체로 변환
        return User.builder()
            .username(accountRecord.getLoginId())        // 로그인 ID
            .password(accountRecord.getEncodedPassword())// 암호화 비밀번호
            .authorities(accountRecord.getRoleName())    // 권한(Role)
            .build();
    }
}
