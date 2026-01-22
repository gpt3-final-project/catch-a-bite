package com.deliveryapp.catchabite.rider.application;

import com.deliveryapp.catchabite.auth.AuthUser;
import com.deliveryapp.catchabite.user.dto.ChangePasswordRequest;
import com.deliveryapp.catchabite.user.dto.ChangePasswordResponse;

/**
 * RiderPasswordService: rider password change contract.
 */
public interface RiderPasswordService {

    ChangePasswordResponse changePassword(AuthUser authUser, ChangePasswordRequest request);
}
