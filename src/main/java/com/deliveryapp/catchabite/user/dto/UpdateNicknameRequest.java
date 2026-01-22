package com.deliveryapp.catchabite.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * UpdateNicknameRequest: nickname update payload.
 */
public record UpdateNicknameRequest(
    @NotBlank
    @Size(min = 2, max = 50)
    String nickname
) {
}
