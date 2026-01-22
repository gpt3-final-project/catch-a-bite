package com.deliveryapp.catchabite.rider.api;

import com.deliveryapp.catchabite.auth.AuthUser;
import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.rider.application.RiderPasswordService;
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
 * RiderPasswordController: endpoint for rider password changes.
 */
@RestController
@RequestMapping("/api/v1/riders/me")
public class RiderPasswordController {

    private final RiderPasswordService riderPasswordService;

    public RiderPasswordController(RiderPasswordService riderPasswordService) {
        this.riderPasswordService = riderPasswordService;
    }

    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<ChangePasswordResponse>> changePassword(
        @AuthenticationPrincipal AuthUser authUser,
        @Valid @RequestBody ChangePasswordRequest request
    ) {
        ChangePasswordResponse response = riderPasswordService.changePassword(authUser, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
