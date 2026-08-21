package com.loopers.application.order;

public record CreateOrderRequest(Long productId, Long quantity) {
    public static CreateOrderRequest of(Long productId, Long quantity) {
        return new CreateOrderRequest(productId, quantity);
    }
}
