package com.deliveryapp.catchabite.converter;

import org.springframework.stereotype.Component;

import com.deliveryapp.catchabite.dto.DelivererDTO;
import com.deliveryapp.catchabite.entity.Deliverer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DelivererConverter {

    public DelivererDTO toDto(Deliverer entity) {
        if (entity == null) return null;

        DelivererDTO delivererDTO = DelivererDTO.builder()
            .delivererId(entity.getDelivererId())
            .delivererLoginId(entity.getDelivererLoginId())
            .delivererLoginPw(entity.getDelivererLoginPw())
            .delivererVehicleType(entity.getDelivererVehicleType())
            .delivererLicenseNumber(entity.getDelivererLicenseNumber())
            .delivererVehicleNumber(entity.getDelivererVehicleNumber())
            .delivererStatus(entity.getDelivererStatus())
            .delivererLastLoginDate(entity.getDelivererLastLoginDate())
            .delivererVerified(entity.getDelivererVerified())
            .build();

        return delivererDTO;
    }

    public Deliverer toEntity(DelivererDTO dto) {
        if(dto == null) return null;

        Deliverer deliverer = Deliverer.builder()
            .delivererId(dto.getDelivererId())
            .delivererLoginId(dto.getDelivererLoginId())
            .delivererLoginPw(dto.getDelivererLoginPw())
            .delivererVehicleType(dto.getDelivererVehicleType())
            .delivererLicenseNumber(dto.getDelivererLicenseNumber())
            .delivererVehicleNumber(dto.getDelivererVehicleNumber())
            .delivererStatus(dto.getDelivererStatus())
            .delivererLastLoginDate(dto.getDelivererLastLoginDate())
            .delivererVerified(dto.getDelivererVerified())
            .build();
        
        return deliverer;
    }
}
