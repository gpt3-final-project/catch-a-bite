package com.deliveryapp.catchabite.converter;

import org.springframework.stereotype.Component;
import com.deliveryapp.catchabite.dto.PaymentDTO;
import com.deliveryapp.catchabite.entity.Payment;
import com.deliveryapp.catchabite.entity.StoreOrder;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentConverter {
    
    public PaymentDTO toDto(Payment entity) {
        if (entity == null) return null;
        
        return PaymentDTO.builder()
                .paymentId(entity.getPaymentId())
                .storeOrderId(entity.getStoreOrder() != null ? entity.getStoreOrder().getOrderId() : null)
                .paymentMethod(entity.getPaymentMethod())
                .paymentAmount(entity.getPaymentAmount())
                .paymentStatus(entity.getPaymentStatus())
                .paymentPaidAt(entity.getPaymentPaidAt())
                .build();
    }
    
    public Payment toEntity(PaymentDTO dto, StoreOrder storeOrder) {
        if (dto == null) return null;
        
        return Payment.builder()
                .paymentId(dto.getPaymentId())
                .storeOrder(storeOrder)
                .paymentMethod(dto.getPaymentMethod())
                .paymentAmount(dto.getPaymentAmount())
                .paymentStatus(dto.getPaymentStatus())
                .paymentPaidAt(dto.getPaymentPaidAt())
                .build();
    }
}
