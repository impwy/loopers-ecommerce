package com.loopers.application;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.OrderRegister;
import com.loopers.domain.order.CreateOrderSpec;
import com.loopers.domain.order.Order;
import com.loopers.interfaces.api.order.dto.OrderV1Dto.Request.CreateOrderRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderRegister orderRegister;

    public Order register(CreateOrderRequest createOrderRequest) {
        CreateOrderSpec createOrderSpec = CreateOrderSpec.of(createOrderRequest.memberId(),
                                                             createOrderRequest.orderNo(),
                                                             createOrderRequest.address());
        return orderRegister.register(createOrderSpec);
    }
}
