package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.deliveryapp.catchabite.auth.domain.AccountStatus;

/**
 * 회원 기본 엔티티
 * APP_USER 테이블과 매핑
 */
@Entity
@Table(name = "APP_USER")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {
        "addresses",
        "favoriteStores",
        "carts",
        "orders",
        "reviews",
        "notifications"
})
public class AppUser {

    // 회원 PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APP_USER_ID")
    private Long appUserId;

    // 로그인 ID
    @Column(name = "APP_USER_LOGIN_ID", nullable = false, unique = true, length = 50)
    private String appUserLoginId;

    // 닉네임
    @Column(name = "APP_USER_NICKNAME", unique = true, length = 50)
    private String appUserNickname;

    // 로그인 비밀번호 (암호화)
    @Column(name = "APP_USER_PASSWORD", nullable = false, length = 255)
    private String appUserPassword;

    // 사용자 이름
    @Column(name = "APP_USER_NAME", nullable = false, length = 100)
    private String appUserName;

    // 생년월일
    @Column(name = "APP_USER_BIRTHDAY")
    private LocalDate appUserBirthday;

    // 성별 (M/F)
    @Column(name = "APP_USER_GENDER", length = 1)
    private String appUserGender;

    // 휴대폰 번호
    @Column(name = "APP_USER_MOBILE", nullable = false, unique = true, length = 11)
    private String appUserMobile;

    // 이메일
    @Column(name = "APP_USER_EMAIL", nullable = false, unique = true, length = 100)
    private String appUserEmail;

    // 가입일
    @Column(name = "APP_USER_CREATED_DATE")
    private LocalDate appUserCreatedDate;

    // 계정 상태
    @Column(name = "APP_USER_STATUS", length = 1)
    private String appUserStatus;

    /* ===============================
       연관관계 (1:N)
       =============================== */

    // 배송지 목록
    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    // 즐겨찾기 가게 목록
    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    @Builder.Default
    private List<FavoriteStore> favoriteStores = new ArrayList<>();

    // 장바구니 목록
    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Cart> carts = new ArrayList<>();

    // 주문 목록
    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    @Builder.Default
    private List<StoreOrder> orders = new ArrayList<>();

    // 리뷰 목록
    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    // 알림 목록
    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();

    // 비밀번호 변경 처리
    public void changePassword(String encodedPassword) {
        this.appUserPassword = encodedPassword;
    }
}
