package com.loopers.domain.orderitem;

import java.math.BigDecimal;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.order.Order;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

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

    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Order order;

    private OrderItem(Order order, Long productId, Long quantity, BigDecimal price) {
        this.order = order;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public static OrderItem create(Order order, Long productId, Long quantity, BigDecimal price) {
        if (order == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 주문입니다..");
        }
        if (productId == null || productId <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품은 필수입니다.");
        }
        if (quantity == null || quantity <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 수량입니다. : " + quantity);
        }
        if (price == null || price.doubleValue() <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 총 가격입니다. : " + price);
        }
        return new OrderItem(order, productId, quantity, price);
    }

    public static OrderItem create(CreateOrderItemSpec createSpec) {
        return create(createSpec.order(), createSpec.productId(), createSpec.quantity(), createSpec.totalPrice());
    }
}
