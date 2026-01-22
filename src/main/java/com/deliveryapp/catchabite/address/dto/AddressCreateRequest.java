package com.deliveryapp.catchabite.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * AddressCreateRequest: payload for adding a new address.
 */
public record AddressCreateRequest(
    @NotBlank
    @Size(max = 255)
    String addressDetail,

    @Size(max = 50)
    String addressNickname,

    @Size(max = 100)
    String addressEntranceMethod,

    Boolean isDefault
) {
}
