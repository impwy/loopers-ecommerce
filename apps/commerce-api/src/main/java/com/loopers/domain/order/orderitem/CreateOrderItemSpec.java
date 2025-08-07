package com.loopers.domain.order.orderitem;

import java.math.BigDecimal;

import com.loopers.domain.order.Order;

public record CreateOrderItemSpec(Long productId, Long quantity) {
    public static CreateOrderItemSpec of(Long productId, Long quantity) {
        return new CreateOrderItemSpec(productId, quantity);
    }
}
