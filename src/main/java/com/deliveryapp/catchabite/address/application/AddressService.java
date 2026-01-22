package com.deliveryapp.catchabite.address.application;

import com.deliveryapp.catchabite.address.dto.AddressCreateRequest;
import com.deliveryapp.catchabite.address.dto.AddressResponse;
import com.deliveryapp.catchabite.address.dto.AddressUpdateRequest;
import com.deliveryapp.catchabite.common.exception.AppException;
import com.deliveryapp.catchabite.common.exception.ErrorCode;
import com.deliveryapp.catchabite.common.util.SecurityUtil;
import com.deliveryapp.catchabite.entity.Address;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.repository.AddressRepository;
import com.deliveryapp.catchabite.repository.AppUserRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AddressService: CRUD for my addresses with ownership checks.
 */
@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final AppUserRepository appUserRepository;

    public AddressService(AddressRepository addressRepository, AppUserRepository appUserRepository) {
        this.addressRepository = addressRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses() {
        AppUser user = getCurrentUser();
        List<Address> addresses = addressRepository.findByAppUserOrderByAddressCreatedDateDesc(user)
            .stream()
            .filter(address -> !isHidden(address))
            .sorted(defaultFirstComparator())
            .collect(Collectors.toList());
        return addresses.stream().map(this::toResponse).toList();
    }

    @Transactional
    public AddressResponse createAddress(AddressCreateRequest request) {
        AppUser user = getCurrentUser();
        String detail = normalizeRequired(request.addressDetail(), "addressDetail");
        String nickname = normalizeOptional(request.addressNickname());
        String entranceMethod = normalizeOptional(request.addressEntranceMethod());
        boolean isDefault = Boolean.TRUE.equals(request.isDefault());

        if (isDefault) {
            clearDefault(user, null);
        }

        Address address = Address.builder()
            .appUser(user)
            .addressDetail(detail)
            .addressNickname(nickname)
            .addressEntranceMethod(entranceMethod)
            .addressIsDefault(isDefault ? "Y" : "N")
            .addressCreatedDate(LocalDate.now())
            .addressVisible("Y")
            .build();

        Address saved = addressRepository.save(address);
        return toResponse(saved);
    }

    @Transactional
    public AddressResponse updateAddress(Long addressId, AddressUpdateRequest request) {
        AppUser user = getCurrentUser();
        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Address not found."));
        ensureOwner(address, user);

        String detail = normalizeOptional(request.addressDetail());
        String nickname = normalizeOptional(request.addressNickname());
        String entranceMethod = normalizeOptional(request.addressEntranceMethod());

        if (request.addressDetail() != null && detail == null) {
            throw new IllegalArgumentException("addressDetail is required.");
        }

        address.updateDetails(detail, nickname, entranceMethod);

        if (request.isDefault() != null) {
            if (request.isDefault()) {
                clearDefault(user, address.getAddressId());
                address.markDefault(true);
            } else {
                address.markDefault(false);
            }
        }

        return toResponse(address);
    }

    @Transactional
    public void deleteAddress(Long addressId) {
        AppUser user = getCurrentUser();
        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Address not found."));
        ensureOwner(address, user);
        addressRepository.delete(address);
    }

    @Transactional
    public AddressResponse setDefault(Long addressId) {
        AppUser user = getCurrentUser();
        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Address not found."));
        ensureOwner(address, user);

        clearDefault(user, addressId);
        address.markDefault(true);

        return toResponse(address);
    }

    private AppUser getCurrentUser() {
        SecurityUtil.CurrentUser currentUser = SecurityUtil.getCurrentUser();
        if (!"USER".equals(currentUser.accountType())) {
            throw new AppException(ErrorCode.FORBIDDEN, "User role required.");
        }
        return appUserRepository
            .findByAppUserEmailOrAppUserMobile(currentUser.loginKey(), currentUser.loginKey())
            .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND, "User not found."));
    }

    private void ensureOwner(Address address, AppUser user) {
        Long ownerId = address.getAppUser() != null ? address.getAppUser().getAppUserId() : null;
        if (!Objects.equals(ownerId, user.getAppUserId())) {
            throw new AppException(ErrorCode.FORBIDDEN, "Forbidden.");
        }
    }

    private void clearDefault(AppUser user, Long keepId) {
        List<Address> addresses = addressRepository.findByAppUser(user);
        for (Address address : addresses) {
            if (address.isDefault()
                && (keepId == null || !keepId.equals(address.getAddressId()))) {
                address.markDefault(false);
            }
        }
    }

    private AddressResponse toResponse(Address address) {
        return new AddressResponse(
            address.getAddressId(),
            address.getAddressDetail(),
            address.getAddressNickname(),
            address.getAddressEntranceMethod(),
            address.isDefault(),
            address.getAddressCreatedDate()
        );
    }

    private Comparator<Address> defaultFirstComparator() {
        return (a, b) -> {
            boolean aDefault = a.isDefault();
            boolean bDefault = b.isDefault();
            if (aDefault != bDefault) {
                return aDefault ? -1 : 1;
            }
            LocalDate aDate = a.getAddressCreatedDate();
            LocalDate bDate = b.getAddressCreatedDate();
            if (aDate == null && bDate == null) {
                return 0;
            }
            if (aDate == null) {
                return 1;
            }
            if (bDate == null) {
                return -1;
            }
            return bDate.compareTo(aDate);
        };
    }

    private boolean isHidden(Address address) {
        String visible = address.getAddressVisible();
        return "N".equalsIgnoreCase(visible);
    }

    private String normalizeRequired(String value, String fieldName) {
        String trimmed = value != null ? value.trim() : "";
        if (trimmed.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return trimmed;
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }
}
