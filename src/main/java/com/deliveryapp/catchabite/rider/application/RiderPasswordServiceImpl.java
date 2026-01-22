package com.deliveryapp.catchabite.rider.application;

import com.deliveryapp.catchabite.auth.AuthUser;
import com.deliveryapp.catchabite.common.exception.AppException;
import com.deliveryapp.catchabite.common.exception.ErrorCode;
import com.deliveryapp.catchabite.common.service.PasswordChangeService;
import com.deliveryapp.catchabite.entity.Deliverer;
import com.deliveryapp.catchabite.repository.DelivererRepository;
import com.deliveryapp.catchabite.user.dto.ChangePasswordRequest;
import com.deliveryapp.catchabite.user.dto.ChangePasswordResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RiderPasswordServiceImpl: handles rider password changes.
 */
@Service
@Transactional
public class RiderPasswordServiceImpl implements RiderPasswordService {

    private final DelivererRepository delivererRepository;
    private final PasswordChangeService passwordChangeService;

    public RiderPasswordServiceImpl(
        DelivererRepository delivererRepository,
        PasswordChangeService passwordChangeService
    ) {
        this.delivererRepository = delivererRepository;
        this.passwordChangeService = passwordChangeService;
    }

    @Override
    public ChangePasswordResponse changePassword(AuthUser authUser, ChangePasswordRequest request) {
        if (authUser == null) {
            throw new AppException(ErrorCode.FORBIDDEN, "Rider authentication required.");
        }
        if (!"RIDER".equals(authUser.accountType())) {
            throw new AppException(ErrorCode.FORBIDDEN, "Rider role required.");
        }

        Deliverer deliverer = delivererRepository
            .findByDelivererEmail(authUser.loginKey())
            .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND, "Rider not found."));

        String encoded = passwordChangeService.validateAndEncode(
            request.currentPassword(),
            request.newPassword(),
            request.confirmNewPassword(),
            deliverer.getDelivererPassword()
        );

        deliverer.changePassword(encoded);
        return new ChangePasswordResponse(true);
    }
}
