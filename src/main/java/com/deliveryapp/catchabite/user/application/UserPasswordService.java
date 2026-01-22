package com.deliveryapp.catchabite.user.application;

import com.deliveryapp.catchabite.auth.AuthUser;
import com.deliveryapp.catchabite.user.dto.ChangePasswordRequest;
import com.deliveryapp.catchabite.user.dto.ChangePasswordResponse;

/**
 * UserPasswordService: password change use case contract.
 */
public interface UserPasswordService {

    ChangePasswordResponse changePassword(AuthUser authUser, ChangePasswordRequest request);
}
