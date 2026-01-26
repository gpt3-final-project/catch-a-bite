package com.deliveryapp.catchabite.controller;

/**
 * AppUserController: 사용자 계정 관리 HTTP 엔드포인트
 * * Description: 앱 사용자의 가입, 조회, 정보 수정, 탈퇴를 처리합니다.
 * * 주요 기능:
 * 1. 사용자 생성 (createUser) - 회원 가입 [POST, Return: AppUserDTO]
 * 2. 사용자 조회 (readUser) - 마이페이지 등 정보 로드 [GET, Return: AppUserDTO]
 * 3. 사용자 수정 (updateUser) - 프로필 변경 [PUT, Return: AppUserDTO]
 * 4. 사용자 삭제 (deleteUser) - 회원 탈퇴 [DELETE, Return: Void]
 */

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.dto.AppUserDTO;
import com.deliveryapp.catchabite.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/appuser")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    /**
     * 신규 사용자를 생성(회원가입)합니다.
     * POST /api/v1/appuser
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AppUserDTO>> createUser(@RequestBody AppUserDTO dto) {
        AppUserDTO createdUser = appUserService.createUser(dto);
        return ResponseEntity.ok(ApiResponse.ok(createdUser));
    }

    /**
     * 사용자 ID로 회원 정보를 조회합니다.
     * GET /api/v1/appuser/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<AppUserDTO>> readUser(@PathVariable Long userId) {
        AppUserDTO user = appUserService.readUser(userId);
        return ResponseEntity.ok(ApiResponse.ok(user));
    }

    /**
     * 사용자 정보를 수정합니다.
     * PUT /api/v1/appuser/{userId}
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<AppUserDTO>> updateUser(@PathVariable Long userId, @RequestBody AppUserDTO dto) {
        AppUserDTO updatedUser = appUserService.updateUser(userId, dto);
        return ResponseEntity.ok(ApiResponse.ok(updatedUser));
    }

    /**
     * 사용자를 삭제(회원탈퇴)합니다.
     * DELETE /api/v1/appuser/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        appUserService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.okMessage("User deleted successfully"));
    }
}