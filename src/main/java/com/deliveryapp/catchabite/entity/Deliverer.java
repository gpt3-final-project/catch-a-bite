package com.deliveryapp.catchabite.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "deliverer",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_deliverer_license_number", columnNames = "deliverer_license_number")
       })
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deliverer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deliverer_id")
    private Long id;

    // 오토바이/자전거/차량/walking
    @Enumerated(EnumType.STRING)
    @Column(name = "deliverer_vehicle_type", length = 50, nullable = false)
    private VehicleType vehicleType;

    @Column(name = "deliverer_license_number", length = 50, unique = true)
    private String licenseNumber;

    @Column(name = "deliverer_vehicle_number", length = 50)
    private String vehicleNumber;

    // Y: 가능 / N: 불가능
    @Enumerated(EnumType.STRING)
    @Column(name = "deliverer_status", length = 1, nullable = false)
    private YesNo status;

    // 마지막 로그인
    @Column(name = "deliverer_last_login_date")
    private LocalDateTime lastLoginDate;

    // 본인인증 여부 (Y/N)
    @Enumerated(EnumType.STRING)
    @Column(name = "deliverer_verified", length = 1, nullable = false)
    private YesNo verified;

    @PrePersist
    void prePersist() {
        if (status == null) status = YesNo.Y;     // Oracle DEFAULT 'Y'
        if (verified == null) verified = YesNo.N; // Oracle DEFAULT 'N'
    }

    public enum VehicleType { MOTORBIKE, BICYCLE, CAR, WALKING }
    public enum YesNo { Y, N }
}