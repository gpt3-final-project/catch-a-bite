package com.deliveryapp.catchabite.entity;

import jakarta.persistence.*;
import lombok.*;

/*
 * 이 클래스는 일부러 @Setter를 작성하지 않았습니다. 
 * @Builder만 사용함으로 null이 발생하는 것을 방지하고자 합니다.
 */
@Entity
@Table(name = "CART_ITEM")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    @Id
    @Column(name="CART_ITEM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CART_ID", nullable = false)
    private Cart cart;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="MENU_ID", nullable = false)
    private Menu menu;

    @Column(name="CART_ITEM_QUANTITY", nullable = false)
    @Builder.Default
    private Integer cartItemQuantity = 1;

    public void changeQuantity(Integer cartItemQuantity) {
        this.cartItemQuantity = cartItemQuantity;
    }

}
