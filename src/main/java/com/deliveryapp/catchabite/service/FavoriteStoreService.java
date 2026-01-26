package com.deliveryapp.catchabite.service;

import java.util.List;

import com.deliveryapp.catchabite.dto.FavoriteStoreDTO;
import com.deliveryapp.catchabite.dto.UserFavoriteStoreResponseDTO;

public interface FavoriteStoreService {
    FavoriteStoreDTO addFavorite(Long storeId, String userLoginId);
    List<UserFavoriteStoreResponseDTO> getMyFavoriteStores(String userLoginId);
    void removeFavorite(Long favoriteId);


}
