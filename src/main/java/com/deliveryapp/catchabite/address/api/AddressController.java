package com.deliveryapp.catchabite.address.api;

import com.deliveryapp.catchabite.address.application.AddressService;
import com.deliveryapp.catchabite.address.dto.AddressCreateRequest;
import com.deliveryapp.catchabite.address.dto.AddressResponse;
import com.deliveryapp.catchabite.address.dto.AddressUpdateRequest;
import com.deliveryapp.catchabite.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AddressController: APIs for managing my addresses.
 */
@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses() {
        return ResponseEntity.ok(ApiResponse.ok(addressService.getMyAddresses()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
        @Valid @RequestBody AddressCreateRequest request
    ) {
        AddressResponse response = addressService.createAddress(request);
        return ResponseEntity.status(201).body(ApiResponse.ok(response));
    }

    @PatchMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
        @PathVariable Long addressId,
        @Valid @RequestBody AddressUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(addressService.updateAddress(addressId, request)));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok(ApiResponse.okMessage("deleted"));
    }

    @PostMapping("/{addressId}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(@PathVariable Long addressId) {
        return ResponseEntity.ok(ApiResponse.ok(addressService.setDefault(addressId)));
    }
}
