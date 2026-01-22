package com.deliveryapp.catchabite.owner.application;

import com.deliveryapp.catchabite.auth.AuthUser;
import com.deliveryapp.catchabite.user.dto.ChangePasswordRequest;
import com.deliveryapp.catchabite.user.dto.ChangePasswordResponse;

/**
 * OwnerPasswordService: store owner password change contract.
 */
public interface OwnerPasswordService {

    ChangePasswordResponse changePassword(AuthUser authUser, ChangePasswordRequest request);
}
