package com.deliveryapp.catchabite.converter;

import com.deliveryapp.catchabite.dto.MenuDTO;
import com.deliveryapp.catchabite.entity.Menu;

import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

@Log4j2
@Component
public class MenuConverter {

    /**
     * Menu 엔티티를 MenuDTO로 변환합니다.
     */
    public MenuDTO toDto(Menu menu, Long storeId, Long menuCategoryId) {
        if (menu == null) {
            log.info("===============================================");
            log.info("menu가 null입니다.");
            log.info("===============================================");
            return null;
        }
        if (storeId == null){
            log.info("===============================================");
            log.info("storeId가 null입니다.");
            log.info("===============================================");
            return null;
        }
        if (menuCategoryId == null){
            log.info("===============================================");
            log.info("menuCategoryId가 null입니다.");
            log.info("===============================================");
            return null;
        }

        return MenuDTO.builder()
            .menuId(menu.getMenuId())
            .storeId(storeId)
            .menuCategoryId(menuCategoryId)
            .menuName(menu.getMenuName())
            .menuDescription(menu.getMenuDescription())
            .menuPrice(menu.getMenuPrice())
            .menuIsAvailable(menu.getMenuIsAvailable())
            .build();
        }
}