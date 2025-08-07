package com.loopers.domain.order.orderitem;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.order.Order;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "order_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {
    private Long productId;

    private Long quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Order order;

    private OrderItem(Order order, Long productId, Long quantity) {
        this.order = order;
        this.productId = productId;
        this.quantity = quantity;
    }

    public static OrderItem create(Order order, Long productId, Long quantity) {
        if (order == null) {
            throw new IllegalArgumentException("잘못된 주문입니다.");
        }
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("상품을 찾을 수 없습니다.");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("잘못된 수량입니다. : " + quantity);
        }
        return new OrderItem(order, productId, quantity);
    }
}
