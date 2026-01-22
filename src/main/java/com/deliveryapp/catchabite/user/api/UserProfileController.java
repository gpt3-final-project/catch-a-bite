package com.deliveryapp.catchabite.user.api;

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.user.application.UserProfileService;
import com.deliveryapp.catchabite.user.dto.UpdateNicknameRequest;
import com.deliveryapp.catchabite.user.dto.UserProfileResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserProfileController: endpoints for updating my profile.
 */
@RestController
@RequestMapping("/api/v1/users/me")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
        @Valid @RequestBody UpdateNicknameRequest request
    ) {
        UserProfileResponse response = userProfileService.updateNickname(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
