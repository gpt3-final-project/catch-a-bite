package com.deliveryapp.catchabite.user.application;

import com.deliveryapp.catchabite.common.exception.AppException;
import com.deliveryapp.catchabite.common.exception.ErrorCode;
import com.deliveryapp.catchabite.common.util.SecurityUtil;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.repository.AppUserRepository;
import com.deliveryapp.catchabite.user.dto.UpdateNicknameRequest;
import com.deliveryapp.catchabite.user.dto.UserProfileResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserProfileService: user profile update logic.
 */
@Service
@Transactional
public class UserProfileService {

    private final AppUserRepository appUserRepository;

    public UserProfileService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public UserProfileResponse updateNickname(UpdateNicknameRequest request) {
        SecurityUtil.CurrentUser currentUser = SecurityUtil.getCurrentUser();
        if (!"USER".equals(currentUser.accountType())) {
            throw new AppException(ErrorCode.FORBIDDEN, "User role required.");
        }

        AppUser user = appUserRepository
            .findByAppUserEmailOrAppUserMobile(currentUser.loginKey(), currentUser.loginKey())
            .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND, "User not found."));

        String newNickname = request.nickname() != null ? request.nickname().trim() : "";
        if (newNickname.isBlank()) {
            throw new IllegalArgumentException("nickname is required.");
        }

        if (!newNickname.equals(user.getAppUserNickname())
            && appUserRepository.existsByAppUserNickname(newNickname)) {
            throw new AppException(ErrorCode.DUPLICATE_NICKNAME, "Nickname already in use.");
        }

        user.changeNickname(newNickname);

        return new UserProfileResponse(
            user.getAppUserId(),
            user.getAppUserEmail(),
            user.getAppUserNickname()
        );
    }
}
