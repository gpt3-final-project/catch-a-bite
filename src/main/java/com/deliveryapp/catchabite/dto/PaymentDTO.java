package com.deliveryapp.catchabite.dto;

import com.deliveryapp.catchabite.common.util.DTOChecker;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PaymentDTO {

    private Long paymentId;                     //PK
    private Long storeOrderId;                  //FK
    private String paymentMethod;
    private Long paymentAmount;
    private String paymentStatus;
    private LocalDateTime paymentPaidAt;

    /**
      * 디버깅용: DTO 매핑 후 null인 필드들을 로그로 출력함.
      * 생성/조회 시 의도치 않은 필드 누락을 감지하기 위해 사용.
      */
    public String nullFieldsReport() {
        return DTOChecker.nullFieldsReport(this);
    }
}
