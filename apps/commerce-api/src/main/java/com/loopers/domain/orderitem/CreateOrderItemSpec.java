package com.loopers.domain.orderitem;

import java.math.BigDecimal;

public record CreateOrderItemSpec(Long orderId, Long productId, Long quantity, BigDecimal totalPrice) {
    public static CreateOrderItemSpec of(Long orderId, Long productId, Long quantity, BigDecimal totalPrice) {
        return new CreateOrderItemSpec(orderId, productId, quantity, totalPrice);
    }
}
