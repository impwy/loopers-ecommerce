package com.loopers.domain.order.orderitem;

public record CreateOrderItemSpec(Long productId, Long quantity, Long couponId) {
    public static CreateOrderItemSpec of(Long productId, Long quantity, Long couponId) {
        return new CreateOrderItemSpec(productId, quantity, couponId);
    }
}
