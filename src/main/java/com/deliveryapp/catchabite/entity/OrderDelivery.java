package com.deliveryapp.catchabite.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_delivery",
       indexes = {
           @Index(name = "idx_order_delivery_order_id", columnList = "order_id"),
           @Index(name = "idx_order_delivery_deliverer_id", columnList = "deliverer_id")
       })
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    // "ORDER"(order_id) FK였지만, 여기서는 일단 orderId만 유지 (엔티티 의존 제거)
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    // 배정 전에는 NULL 가능
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "deliverer_id"
    )
    private Deliverer deliverer;

    // 배달 대행 수락한 시간
    @Column(name = "order_accept_time")
    private LocalDateTime orderAcceptTime;

    // 가게에서 픽업 시간
    @Column(name = "order_delivery_pickup_time")
    private LocalDateTime pickupTime;

    // 배달 시작/완료 시간
    @Column(name = "order_delivery_start_time")
    private LocalDateTime startTime;

    @Column(name = "order_delivery_complete_time")
    private LocalDateTime completeTime;

    // 거리/시간(분)
    @Column(name = "order_delivery_distance")
    private Integer distanceKm;

    @Column(name = "order_delivery_est_time")
    private Integer estimatedMinutes;

    @Column(name = "order_delivery_act_time")
    private Integer actualMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_delivery_status", length = 20, nullable = false)
    private DeliveryStatus status;

    @Column(name = "order_delivery_created_date", nullable = false)
    private LocalDateTime createdDate;

    @PrePersist
    void prePersist() {
        if (status == null) status = DeliveryStatus.PENDING;
        if (createdDate == null) createdDate = LocalDateTime.now();
    }

    public enum DeliveryStatus {
        PENDING, ASSIGNED, PICKED_UP, IN_DELIVERY, DELIVERED, CANCELLED
    }
}