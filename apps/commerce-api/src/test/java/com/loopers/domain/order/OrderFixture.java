package com.loopers.domain.order;

public record OrderFixture() {
    public static Order createOrder() {
        return Order.create(CreateOrderSpec.of(1L));
    }
}
