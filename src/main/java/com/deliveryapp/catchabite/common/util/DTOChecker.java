package com.deliveryapp.catchabite.common.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * DTOChecker: DTO 필드 null 검증 유틸리티
 * 
 * Description: 엔티티를 DTO로 변환한 후 의도치 않은 필드 누락(null)을 감지하기 위한
 * 검증 유틸. 모든 DTO가 공통으로 사용하는 디버깅 도구.
 * 
 * Required Variables/Parameters: 없음 (static utility)
 * 
 * Output/Data Flow:
 * - nullFieldsReport() 메서드: "DTOClass nullFields=[field1, field2, ...]" 형식의 문자열 반환
 * - 정적 메서드로만 제공되며, 인스턴스 생성 불가 (private constructor)
 */
public final class DTOChecker {
    private DTOChecker() {}

    /**
     * DTO의 null인 필드명들을 수집하여 디버깅용 문자열로 반환한다.
     * 
     * 목적: DTO에서 @Builder 생성 후 예상 외 필드가 null이면 즉시 감지 가능.
     * 
     * 처리 흐름:
     * 1. 입력 DTO가 null이면 "DTO is null" 반환
     * 2. getDeclaredFields()로 DTO 클래스의 모든 필드 조회
     * 3. Static 필드 제외 (static은 인스턴스 필드가 아니므로 검증 대상 아님)
     * 4. setAccessible(true) 후 Field.get()으로 각 필드값 읽음
     * 5. null인 필드명만 수집하여 리스트 생성
     * 6. "클래스명 nullFields=[필드1, 필드2, ...]" 형식으로 반환
     */
    public static String nullFieldsReport(Object dto) {
        if (dto == null) return "DTO is null";

        List<String> nullFields = new ArrayList<>();
        Field[] fields = dto.getClass().getDeclaredFields();

        for (Field f : fields) {
            // Static 필드는 인스턴스 필드가 아니므로 제외
            if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;

            try {
                // private 필드도 접근 가능하게 설정
                f.setAccessible(true); 
                // 해당 객체의 필드값 읽음
                Object value = f.get(dto); 
                if (value == null) nullFields.add(f.getName());
            } catch (IllegalAccessException e) {
                // 예외 발생 필드도 기록 (디버깅 추적용)
                nullFields.add(f.getName() + "(unreadable)");
            }
        }

        return dto.getClass().getSimpleName() + " nullFields=" + nullFields;
    }
}
