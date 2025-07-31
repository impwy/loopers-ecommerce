package com.loopers.domain.orderitem;

import java.math.BigDecimal;

public record CreateOrderItemSpec(Long orderId, Long productId, Long quantity, BigDecimal price) {
    public static CreateOrderItemSpec of(Long orderId, Long productId, Long quantity, BigDecimal price) {
        return new CreateOrderItemSpec(orderId, productId, quantity, price);
    }
}
