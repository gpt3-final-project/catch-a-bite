package com.deliveryapp.catchabite.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.deliveryapp.catchabite.entity.Review;
public interface ReviewRepository extends JpaRepository<Review,Long> {    

    Optional<Review> findByStoreOrderOrderId(Long storeOrderId);

    //Review findByStore_StoreId(Long storeId, Pageable pageable)

    //Review findByReviewIdAndStore_StoreId(Long reviewId, Long storeId)

    long countByStore_StoreId(Long storeId);
}
