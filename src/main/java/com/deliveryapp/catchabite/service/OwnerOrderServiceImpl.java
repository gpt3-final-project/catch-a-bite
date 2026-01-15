package com.deliveryapp.catchabite.service;

import com.deliveryapp.catchabite.domain.enumtype.OrderStatus;
import com.deliveryapp.catchabite.dto.OwnerOrderDTO;
import com.deliveryapp.catchabite.entity.StoreOrder;
import com.deliveryapp.catchabite.repository.StoreOrderRepository;
import com.deliveryapp.catchabite.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OwnerOrderServiceImpl implements OwnerOrderService {

	private final StoreRepository storeRepository;
	private final StoreOrderRepository storeOrderRepository;

	@Override
	@Transactional(readOnly = true)
	public List<OwnerOrderDTO> listOrders(Long storeOwnerId, Long storeId, String status) {

		validateOwnedStore(storeOwnerId, storeId);

		List<StoreOrder> orders;

		if (status == null || status.isBlank()) {
			orders = storeOrderRepository.findAllByStore_StoreIdOrderByOrderDateDesc(storeId);
		} else {
			OrderStatus orderStatus = parseStatus(status);
			orders = storeOrderRepository.findAllByStore_StoreIdAndOrderStatusOrderByOrderDateDesc(storeId, orderStatus);
		}

		// 목록은 최소 필드만 채워 반환(상세 전용 필드는 detail에서)
		return orders.stream()
				.map(o -> OwnerOrderDTO.builder()
						.orderId(o.getOrderId())
						.storeId(o.getStore().getStoreId())
						.orderStatus(o.getOrderStatus().name())
						.orderCreatedAt(o.getOrderDate())
						.orderTotalPrice(o.getOrderTotalPrice())
						.deliveryFee(o.getOrderDeliveryFee())
						.orderAddress(o.getOrderAddressSnapshot())
						.build())
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public OwnerOrderDTO getOrderDetail(Long storeOwnerId, Long storeId, Long orderId) {

		StoreOrder order = getOwnedOrder(storeOwnerId, storeId, orderId);

		// 현재는 store_order만 있어서 음식/옵션(items)은 다음 단계에서 확장
		return OwnerOrderDTO.builder()
				.orderId(order.getOrderId())
				.storeId(order.getStore().getStoreId())
				.orderStatus(order.getOrderStatus().name())
				.orderCreatedAt(order.getOrderDate())
				.orderTotalPrice(order.getOrderTotalPrice())
				.deliveryFee(order.getOrderDeliveryFee())
				.orderAddress(order.getOrderAddressSnapshot())
				.build();
	}

	@Override
	public void acceptOrder(Long storeOwnerId, Long storeId, Long orderId) {

		StoreOrder order = getOwnedOrder(storeOwnerId, storeId, orderId);

		// pending -> cooking
		if (order.getOrderStatus() != OrderStatus.PENDING) {
			throw new IllegalArgumentException("invalid order status");
		}

		order.changeStatus(OrderStatus.COOKING);
	}

	@Override
	public void rejectOrder(Long storeOwnerId, Long storeId, Long orderId, String reason) {

		StoreOrder order = getOwnedOrder(storeOwnerId, storeId, orderId);

		// pending -> rejected
		if (order.getOrderStatus() != OrderStatus.PENDING) {
			throw new IllegalArgumentException("invalid order status");
		}

		// ERD에 reject_reason 컬럼이 없으면 저장 불가.
		// (지금 StoreOrder에도 필드가 없으니 status 변경만 수행)
		order.changeStatus(OrderStatus.REJECTED);

		// 사유 저장이 필요하면:
		// 1) store_order에 reject_reason 컬럼 추가 + StoreOrder 필드 추가
		// 또는 2) 거절 사유 테이블 별도 생성
	}

	@Override
	public void markCooked(Long storeOwnerId, Long storeId, Long orderId) {

		StoreOrder order = getOwnedOrder(storeOwnerId, storeId, orderId);

		// cooking -> cooked
		if (order.getOrderStatus() != OrderStatus.COOKING) {
			throw new IllegalArgumentException("invalid order status");
		}

		order.changeStatus(OrderStatus.COOKED);
	}

	@Override
	public void markDelivered(Long storeOwnerId, Long storeId, Long orderId) {

		StoreOrder order = getOwnedOrder(storeOwnerId, storeId, orderId);

		// cooked -> delivered
		if (order.getOrderStatus() != OrderStatus.COOKED) {
			throw new IllegalArgumentException("invalid order status");
		}

		order.changeStatus(OrderStatus.DELIVERED);
	}

	// -------------------------
	// private helpers
	// -------------------------

	private void validateOwnedStore(Long storeOwnerId, Long storeId) {
		boolean owned = storeRepository.existsByStoreIdAndStoreOwner_StoreOwnerId(storeId, storeOwnerId);
		if (!owned) {
			throw new IllegalArgumentException("not your store");
		}
	}

	private StoreOrder getOwnedOrder(Long storeOwnerId, Long storeId, Long orderId) {

		validateOwnedStore(storeOwnerId, storeId);

		return storeOrderRepository.findByOrderIdAndStore_StoreId(orderId, storeId)
				.orElseThrow(() -> new IllegalArgumentException("order not found"));
	}

	private OrderStatus parseStatus(String status) {
		try {
			return OrderStatus.valueOf(status.trim().toUpperCase());
		} catch (Exception e) {
			throw new IllegalArgumentException("invalid status");
		}
	}
}
