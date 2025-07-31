package com.loopers.domain.order;

public record CreateOrderSpec(Long memberId, String orderNo) {
    public static CreateOrderSpec of(Long memberId, String orderNo) {
        return new CreateOrderSpec(memberId, orderNo);
    }
}
