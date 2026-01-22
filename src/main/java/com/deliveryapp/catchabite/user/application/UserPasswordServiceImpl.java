package com.deliveryapp.catchabite.user.application;

import com.deliveryapp.catchabite.auth.AuthUser;
import com.deliveryapp.catchabite.common.exception.AppException;
import com.deliveryapp.catchabite.common.exception.ErrorCode;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.repository.AppUserRepository;
import com.deliveryapp.catchabite.user.dto.ChangePasswordRequest;
import com.deliveryapp.catchabite.user.dto.ChangePasswordResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserPasswordServiceImpl: handles password change for authenticated users.
 */
@Service
@Transactional
public class UserPasswordServiceImpl implements UserPasswordService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserPasswordServiceImpl(
        AppUserRepository appUserRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ChangePasswordResponse changePassword(AuthUser authUser, ChangePasswordRequest request) {
        if (authUser == null) {
            throw new AppException(ErrorCode.FORBIDDEN, "User authentication required.");
        }
        if (!"USER".equals(authUser.accountType())) {
            throw new AppException(ErrorCode.FORBIDDEN, "User role required.");
        }

        String loginKey = authUser.loginKey();
        AppUser user = appUserRepository
            .findByAppUserEmailOrAppUserMobile(loginKey, loginKey)
            .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND, "User not found."));

        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new IllegalArgumentException("Password confirmation does not match.");
        }

        if (!passwordEncoder.matches(request.currentPassword(), user.getAppUserPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getAppUserPassword())) {
            throw new IllegalArgumentException("New password must be different.");
        }

        user.changePassword(passwordEncoder.encode(request.newPassword()));
        return new ChangePasswordResponse(true);
    }
}
