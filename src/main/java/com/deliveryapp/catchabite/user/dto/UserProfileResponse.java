package com.deliveryapp.catchabite.user.dto;

/**
 * UserProfileResponse: minimal profile info for my page header.
 */
public record UserProfileResponse(
    Long userId,
    String loginId,
    String nickname
) {
}
