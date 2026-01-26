package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.domain.enumtype.StoreOpenStatus;
import com.deliveryapp.catchabite.entity.Store;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    /**
     * 사업자(오너) 소유 매장 단건 조회 (권한 체크 포함)
     */
    Optional<Store> findByStoreIdAndStoreOwner_StoreOwnerId(Long storeId, Long storeOwnerId);

    /**
     * 사업자(오너) 소유 매장 목록 조회
     */
    List<Store> findAllByStoreOwner_StoreOwnerId(Long storeOwnerId);

    /**
     * 사업자(오너) 소유 여부만 빠르게 체크
     */
    boolean existsByStoreIdAndStoreOwner_StoreOwnerId(Long storeId, Long storeOwnerId);

    /* 
     * 사용자 - 가게 명 및 음식 분류로 검색
     */ 
    List<Store> findByStoreNameContainingIgnoreCaseOrStoreCategoryContainingIgnoreCase(String name, String category);

    /* 
     * 사용자 - 음식 분류로 검색
     */
    List<Store> findByStoreCategory(String category);

    /**
     * 사용자 -  영업 중인 가게 조회
     */
    List<Store> findByStoreOpenStatus(StoreOpenStatus status);
}
