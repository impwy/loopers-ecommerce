package com.loopers.interfaces.api.order.dto;

import com.loopers.domain.order.Address;

public class OrderV1Dto {
    public class Request {
        public record CreateOrderRequest(Long memberId, String orderNo, Address address) {
            public static CreateOrderRequest of(Long memberId, String orderNo, Address address) {
                return new CreateOrderRequest(memberId, orderNo, address);
            }
        }
    }
}
