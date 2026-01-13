package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.StoreOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * StoreOrderRepository: Data access layer for StoreOrder entity
 * 
 * Description: JPA repository for CRUD operations on storeorder table.
 * Provides derived query methods for finding orders by orderId or userId.
 * 
 * Required Variables/Parameters:
 * - StoreOrder entity: Mapped to storeorder table in DB
 * - Long: StoreOrder primary key type (orderId)
 * 
 * Output/Data Flow:
 * - Sends StoreOrder entities to PaymentService for order data retrieval
 * - Returns order records from DB for verification and status updates
 * 
 * Dependencies: Spring Data JPA, StoreOrder entity
 */
public interface StoreOrderRepository extends JpaRepository<StoreOrder, Long> {

    /**
     * Find order by orderId
     * Used to retrieve the order being paid for verification
     */
    Optional<StoreOrder> findByOrderId(Long orderId);
}
