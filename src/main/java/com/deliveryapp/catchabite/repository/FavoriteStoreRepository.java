package com.deliveryapp.catchabite.repository;

import com.deliveryapp.catchabite.entity.FavoriteStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteStoreRepository extends JpaRepository<FavoriteStore, Long> {
    // 특정 사용자의 즐겨찾기 목록 조회
    List<FavoriteStore> findByAppUser_AppUserId(Long appUserId);
    
    // 이미 즐겨찾기 등록되었는지 확인
    boolean existsByAppUser_AppUserIdAndStore_StoreId(Long appUserId, Long storeId);

    Optional<FavoriteStore> findByAppUser_AppUserIdAndStore_StoreId(Long appUserId, Long storeId);

    // Fetch FavoriteStore + Store를 사용해서 N+1을 방지함
    @Query("SELECT fs FROM FavoriteStore fs JOIN FETCH fs.store WHERE fs.appUser.id = :userId")
    List<FavoriteStore> findAllByAppUserId(@Param("userId") Long userId);
}