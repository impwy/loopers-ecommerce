package com.loopers.domain.orderitem;

import java.math.BigDecimal;

import com.loopers.domain.order.Order;

public record CreateOrderItemSpec(Order order, Long productId, Long quantity, BigDecimal totalPrice) {
    public static CreateOrderItemSpec of(Order order, Long productId, Long quantity, BigDecimal totalPrice) {
        return new CreateOrderItemSpec(order, productId, quantity, totalPrice);
    }
}
