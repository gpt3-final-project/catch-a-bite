package com.deliveryapp.catchabite.common.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * PasswordChangeService: validates and encodes password changes.
 */
@Service
public class PasswordChangeService {

    private final PasswordEncoder passwordEncoder;

    public PasswordChangeService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String validateAndEncode(
        String currentPassword,
        String newPassword,
        String confirmNewPassword,
        String encodedPassword
    ) {
        if (!newPassword.equals(confirmNewPassword)) {
            throw new IllegalArgumentException("Password confirmation does not match.");
        }

        if (!passwordEncoder.matches(currentPassword, encodedPassword)) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        if (passwordEncoder.matches(newPassword, encodedPassword)) {
            throw new IllegalArgumentException("New password must be different.");
        }

        return passwordEncoder.encode(newPassword);
    }
}
