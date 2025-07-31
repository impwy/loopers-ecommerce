package com.loopers.domain.order;

public record CreateOrderSpec(Long memberId, String orderNo, Address address) {
    public static CreateOrderSpec of(Long memberId, String number, Address address) {
        return new CreateOrderSpec(memberId, number, address);
    }
}
