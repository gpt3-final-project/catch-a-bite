package com.deliveryapp.catchabite.owner.api;

import com.deliveryapp.catchabite.auth.AuthUser;
import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.owner.application.OwnerPasswordService;
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
 * OwnerPasswordController: endpoint for store owner password changes.
 */
@RestController
@RequestMapping("/api/v1/owners/me")
public class OwnerPasswordController {

    private final OwnerPasswordService ownerPasswordService;

    public OwnerPasswordController(OwnerPasswordService ownerPasswordService) {
        this.ownerPasswordService = ownerPasswordService;
    }

    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<ChangePasswordResponse>> changePassword(
        @AuthenticationPrincipal AuthUser authUser,
        @Valid @RequestBody ChangePasswordRequest request
    ) {
        ChangePasswordResponse response = ownerPasswordService.changePassword(authUser, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
