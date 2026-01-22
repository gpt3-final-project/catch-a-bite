package com.deliveryapp.catchabite.owner.application;

import com.deliveryapp.catchabite.auth.AuthUser;
import com.deliveryapp.catchabite.common.exception.AppException;
import com.deliveryapp.catchabite.common.exception.ErrorCode;
import com.deliveryapp.catchabite.common.service.PasswordChangeService;
import com.deliveryapp.catchabite.entity.StoreOwner;
import com.deliveryapp.catchabite.repository.StoreOwnerRepository;
import com.deliveryapp.catchabite.user.dto.ChangePasswordRequest;
import com.deliveryapp.catchabite.user.dto.ChangePasswordResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OwnerPasswordServiceImpl: handles store owner password changes.
 */
@Service
@Transactional
public class OwnerPasswordServiceImpl implements OwnerPasswordService {

    private final StoreOwnerRepository storeOwnerRepository;
    private final PasswordChangeService passwordChangeService;

    public OwnerPasswordServiceImpl(
        StoreOwnerRepository storeOwnerRepository,
        PasswordChangeService passwordChangeService
    ) {
        this.storeOwnerRepository = storeOwnerRepository;
        this.passwordChangeService = passwordChangeService;
    }

    @Override
    public ChangePasswordResponse changePassword(AuthUser authUser, ChangePasswordRequest request) {
        if (authUser == null) {
            throw new AppException(ErrorCode.FORBIDDEN, "Owner authentication required.");
        }
        if (!"OWNER".equals(authUser.accountType())) {
            throw new AppException(ErrorCode.FORBIDDEN, "Owner role required.");
        }

        StoreOwner owner = storeOwnerRepository
            .findByStoreOwnerEmail(authUser.loginKey())
            .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND, "Owner not found."));

        String encoded = passwordChangeService.validateAndEncode(
            request.currentPassword(),
            request.newPassword(),
            request.confirmNewPassword(),
            owner.getStoreOwnerPassword()
        );

        owner.changePassword(encoded);
        return new ChangePasswordResponse(true);
    }
}
