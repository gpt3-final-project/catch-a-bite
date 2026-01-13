package com.deliveryapp.catchabite.entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "deliverer_payment",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_deliverer_payment_deliverer", columnNames = "deliverer_id")
       })
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DelivererPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deliverer_payment_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "deliverer_id",
            nullable = false
    )
    private Deliverer deliverer;

    @Column(name = "deliverer_payment_minimum_fee", nullable = false)
    private BigDecimal minimumFee;

    @Column(name = "deliverer_payment_distance_fee")
    private BigDecimal distanceFee;
}