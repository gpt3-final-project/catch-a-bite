package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * 이 클래스는 일부러 @Setter를 작성하지 않았습니다. 
 * @Builder만 사용함으로 null이 발생하는 것을 방지하고자 합니다.
 */
@Entity
@Table(name = "REVIEW")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @Column(name = "REVIEW_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ORDER_ID", nullable = false)
    private StoreOrder storeOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="APP_USER_ID", nullable = false)
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="STORE_ID", nullable = false)
    private Store store;

    @Column(name="REVIEW_RATING", nullable = false)
    private BigDecimal reviewRating;

    @Column(name="REVIEW_CONTENT", length = 1000)
    private String reviewContent;

    @Column(name="REVIEW_CREATED_AT", nullable = false)
    private LocalDateTime reviewCreatedAt;

    /**
     * 주문 최초 저장(INSERT) 직전에 reviewCreatedAt를 자동 세팅합니다.
     * 이미 reviewCreatedAt이 지정된 경우(외부 입력/특수 케이스)에는 덮어쓰이지 않습니다.
     */
    @PrePersist
    private void prePersist() {
        if (reviewCreatedAt == null) 
        {
            reviewCreatedAt = LocalDateTime.now();
        }
    }
}