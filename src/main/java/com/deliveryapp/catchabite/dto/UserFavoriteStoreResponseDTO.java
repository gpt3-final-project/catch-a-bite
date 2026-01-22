package com.deliveryapp.catchabite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFavoriteStoreResponseDTO {
    private Long favoriteId;      // PK
    private Long storeId;         // 즐겨찾기 가게의 PK
    private String storeName;     // 가게 이름
    private Double rating;        // Rating
}