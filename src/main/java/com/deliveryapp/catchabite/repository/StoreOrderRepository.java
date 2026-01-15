package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.domain.enumtype.OrderStatus;
import com.deliveryapp.catchabite.entity.StoreOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreOrderRepository extends JpaRepository<StoreOrder, Long> {

	Optional<StoreOrder> findByOrderIdAndStore_StoreId(Long orderId, Long storeId);

	List<StoreOrder> findAllByStore_StoreIdOrderByOrderDateDesc(Long storeId);

	List<StoreOrder> findAllByStore_StoreIdAndOrderStatusOrderByOrderDateDesc(Long storeId, OrderStatus orderStatus);
}
