package com.loopers.domain.order;

public record CreateOrderSpec(Long memberId) {
    public static CreateOrderSpec of(Long memberId) {
        return new CreateOrderSpec(memberId);
    }
}
