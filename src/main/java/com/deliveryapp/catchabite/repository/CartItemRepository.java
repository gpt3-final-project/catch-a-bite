package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // cartId와 menuId로 CartItem 찾습니다.
    // 존재하면 CartItem의 정보가
    // 없으면 Optional.empty()
    Optional<CartItem> findByCart_CartIdAndMenu_MenuId(Long cartId, Long menuId);

    // 카트에 있는 모든 메뉴 아이템을 List로 받습니다.
    List<CartItem> findAllByCart_CartId(Long cartId);

    // 카트에 있는 모든 아이템을 삭제합니다.
    void deleteAllByCart_CartId(Long cartId);
}
