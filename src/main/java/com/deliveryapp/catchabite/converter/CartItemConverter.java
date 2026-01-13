package com.deliveryapp.catchabite.converter;

import org.springframework.stereotype.Component;
import com.deliveryapp.catchabite.dto.CartItemDTO;
import com.deliveryapp.catchabite.entity.CartItem;
import com.deliveryapp.catchabite.entity.Cart;
import com.deliveryapp.catchabite.entity.Menu;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartItemConverter {
    
    public CartItemDTO toDto(CartItem entity) {
        if (entity == null) return null;
        
        return CartItemDTO.builder()
                .cartItemId(entity.getCartItemId())
                .cartId(entity.getCart() != null ? entity.getCart().getCartId() : null)
                .menuId(entity.getMenu() != null ? entity.getMenu().getMenuId() : null)
                .cartItemQuantity(entity.getCartItemQuantity())
                .build();
    }
    
    public CartItem toEntity(CartItemDTO dto, Cart cart, Menu menu) {
        if (dto == null) return null;
        
        return CartItem.builder()
                .cartItemId(dto.getCartItemId())
                .cart(cart)
                .menu(menu)
                .cartItemQuantity(dto.getCartItemQuantity())
                .build();
    }
}
