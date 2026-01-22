package com.deliveryapp.catchabite.user.api;

import com.deliveryapp.catchabite.auth.AuthUser;
import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.user.application.UserPasswordService;
import com.deliveryapp.catchabite.user.dto.ChangePasswordRequest;
import com.deliveryapp.catchabite.user.dto.ChangePasswordResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserPasswordController: endpoint for changing my password.
 */
@RestController
@RequestMapping("/api/v1/users/me")
public class UserPasswordController {

    private final UserPasswordService userPasswordService;

    public UserPasswordController(UserPasswordService userPasswordService) {
        this.userPasswordService = userPasswordService;
    }

    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<ChangePasswordResponse>> changePassword(
        @AuthenticationPrincipal AuthUser authUser,
        @Valid @RequestBody ChangePasswordRequest request
    ) {
        ChangePasswordResponse response = userPasswordService.changePassword(authUser, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
