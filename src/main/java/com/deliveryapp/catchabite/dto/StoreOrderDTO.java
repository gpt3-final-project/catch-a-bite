package com.deliveryapp.catchabite.dto;

import com.deliveryapp.catchabite.common.util.DTOChecker;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StoreOrderDTO {

    private Long orderId;                   //PK
    private Long appUserId;                 //FK
    private Long storeId;                   //FK
    private Long addressId;                 //FK

    private String orderAddressSnapshot;
    private Integer orderTotalPrice;
    private Integer orderDeliveryFee;
    private String orderStatus;
    private LocalDateTime orderDate;

    private Long paymentId;                 //FK - DB에 없음
    private Long reviewId;                  //FK - DB에 없음
    private Long orderDeliveryId;           //FK - DB에 없음



    /**
      * 디버깅용: DTO 매핑 후 null인 필드들을 로그로 출력함.
      * 생성/조회 시 의도치 않은 필드 누락을 감지하기 위해 사용.
      */
    public String nullFieldsReport() {
        return DTOChecker.nullFieldsReport(this);
    }
}
