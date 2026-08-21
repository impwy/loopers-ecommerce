package com.loopers.application.order;

import java.util.List;

public record CreateOrderWithCouponRequest(List<CreateOrderRequest> createOrderRequests, Long couponId) {
    public static CreateOrderWithCouponRequest create(List<CreateOrderRequest> createOrderRequests, Long couponId) {
        return new CreateOrderWithCouponRequest(createOrderRequests, couponId);
    }
}
