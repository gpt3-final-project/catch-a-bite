package com.deliveryapp.catchabite.address.dto;

import java.time.LocalDate;

/**
 * AddressResponse: read model for my address list.
 */
public record AddressResponse(
    Long addressId,
    String addressDetail,
    String addressNickname,
    String addressEntranceMethod,
    boolean isDefault,
    LocalDate createdDate
) {
}
