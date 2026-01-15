package com.deliveryapp.catchabite.entity;

import com.deliveryapp.catchabite.domain.enumtype.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "store_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class StoreOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id", nullable = false)
	private Long orderId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "app_user_id")
	private AppUser appUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "address_id", nullable = false)
	private Address address;

	@Column(name = "order_address_snapshot", length = 255)
	private String orderAddressSnapshot;

	@Column(name = "order_total_price", nullable = false)
	private Integer orderTotalPrice;

	@Column(name = "order_delivery_fee", nullable = false)
	private Integer orderDeliveryFee;

	@Enumerated(EnumType.STRING)
	@Column(name = "order_status", nullable = false, length = 50)
	private OrderStatus orderStatus;

	@Column(name = "order_date", nullable = false)
	private LocalDateTime orderDate;

	@PrePersist
	public void prePersist() {
		if (this.orderDeliveryFee == null) this.orderDeliveryFee = 0;
		if (this.orderStatus == null) this.orderStatus = OrderStatus.PENDING;
		if (this.orderDate == null) this.orderDate = LocalDateTime.now();
	}

	public void changeStatus(OrderStatus nextStatus) {
		this.orderStatus = nextStatus;
	}
}
