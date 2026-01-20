package com.deliveryapp.catchabite.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menu_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MenuCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_category_id", nullable = false)
    private Long menuCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "menu_category_name")
    private String menuCategoryName;

    //사용자 화면에 필요한 가게>메뉴 목록>메뉴를 자세히 나누기 위하여 필요합니다.
    @OneToMany(mappedBy = "menuCategory", fetch = FetchType.LAZY)
    @Builder.Default // 빌더 사용 시 기본값 유지
    private List<Menu> menus = new ArrayList<>();

    public void changeName(String menuCategoryName) {
    this.menuCategoryName = menuCategoryName;
}

}
