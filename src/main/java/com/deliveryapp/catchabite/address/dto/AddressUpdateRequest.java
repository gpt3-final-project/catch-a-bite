package com.deliveryapp.catchabite.address.dto;

import jakarta.validation.constraints.Size;

/**
 * AddressUpdateRequest: payload for updating an address.
 */
public record AddressUpdateRequest(
    @Size(max = 255)
    String addressDetail,

    @Size(max = 50)
    String addressNickname,

    @Size(max = 100)
    String addressEntranceMethod,

    Boolean isDefault
) {
}
