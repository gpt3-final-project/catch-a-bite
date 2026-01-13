package com.deliveryapp.catchabite.converter;

import org.springframework.stereotype.Component;
import com.deliveryapp.catchabite.dto.StoreOrderDTO;
import com.deliveryapp.catchabite.entity.StoreOrder;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.entity.Store;
import com.deliveryapp.catchabite.entity.Address;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StoreOrderConverter {
    
    public StoreOrderDTO toDto(StoreOrder entity) {
        if (entity == null) return null;
        
        return StoreOrderDTO.builder()
                .orderId(entity.getOrderId())
                .appUserId(entity.getAppUser().getAppUserId())
                .storeId(entity.getStore() != null ? entity.getStore().getStoreId() : null)
                .addressId(entity.getAddress() != null ? entity.getAddress().getAddressId() : null)
                .orderAddressSnapshot(entity.getOrderAddressSnapshot())
                .orderTotalPrice(entity.getOrderTotalPrice())
                .orderDeliveryFee(entity.getOrderDeliveryFee())
                .orderStatus(entity.getOrderStatus())
                .orderDate(entity.getOrderDate())
                .build();
    }
    
    public StoreOrder toEntity(StoreOrderDTO dto, AppUser appUser, 
                               Store store, Address address) {
        if (dto == null) return null;
        
        return StoreOrder.builder()
                .orderId(dto.getOrderId())
                .appUser(appUser)
                .store(store)
                .address(address)
                .orderAddressSnapshot(dto.getOrderAddressSnapshot())
                .orderTotalPrice(dto.getOrderTotalPrice())
                .orderDeliveryFee(dto.getOrderDeliveryFee())
                .orderStatus(dto.getOrderStatus())
                .orderDate(dto.getOrderDate())
                .build();
    }
}
