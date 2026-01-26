package com.deliveryapp.catchabite.controller;

/**
 * AddressController: 사용자 배송지 관리 HTTP 엔드포인트
 * * Description: 사용자의 배송지 주소를 추가, 조회, 수정, 삭제하는 기능을 제공합니다.
 * * 주요 기능:
 * 1. 배송지 추가 (createAddress) - 새로운 주소 등록 [POST, Return: AddressDTO]
 * 2. 배송지 조회 (readAddress) - 특정 주소 상세 정보 조회 [GET, Return: AddressDTO]
 * 3. 배송지 수정 (updateAddress) - 주소 정보 변경 [PUT, Return: AddressDTO]
 * 4. 배송지 삭제 (deleteAddress) - 주소 삭제 처리 [DELETE, Return: Void]
 */

import com.deliveryapp.catchabite.common.response.ApiResponse;
import com.deliveryapp.catchabite.dto.AddressDTO;
import com.deliveryapp.catchabite.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/appuser/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    /**
     * 새로운 배송지를 등록합니다.
     * POST /api/v1/appuser/addresses
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AddressDTO>> createAddress(@RequestBody AddressDTO dto) {
        AddressDTO createdAddress = addressService.createAddress(dto);
        return ResponseEntity.ok(ApiResponse.ok(createdAddress));
    }

    /**
     * 특정 배송지의 상세 정보를 조회합니다.
     * GET /api/v1/appuser/addresses/{addressId}
     */
    @GetMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressDTO>> readAddress(@PathVariable Long addressId) {
        AddressDTO address = addressService.readAddress(addressId);
        return ResponseEntity.ok(ApiResponse.ok(address));
    }

    /**
     * 기존 배송지 정보를 수정합니다.
     * PUT /api/v1/appuser/addresses/{addressId}
     */
    @PutMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressDTO>> updateAddress(@PathVariable Long addressId, @RequestBody AddressDTO dto) {
        AddressDTO updatedAddress = addressService.updateAddress(addressId, dto);
        return ResponseEntity.ok(ApiResponse.ok(updatedAddress));
    }

    /**
     * 배송지 정보를 삭제합니다.
     * DELETE /api/v1/appuser/addresses/{addressId}
     */
    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok(ApiResponse.okMessage("Address deleted successfully"));
    }
}