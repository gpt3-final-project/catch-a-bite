package com.deliveryapp.catchabite.user.dto;

/**
 * ChangePasswordResponse: indicates whether password change succeeded.
 */
public record ChangePasswordResponse(
    boolean changed
) {
}
