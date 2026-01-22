package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.Address;
import com.deliveryapp.catchabite.entity.AppUser;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByAppUser(AppUser appUser);

    List<Address> findByAppUserOrderByAddressCreatedDateDesc(AppUser appUser);
}


