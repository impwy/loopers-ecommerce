package com.loopers.application.order;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.OrderRegister;
import com.loopers.application.required.OrderRepository;
import com.loopers.domain.order.CreateOrderSpec;
import com.loopers.domain.order.Order;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderModifyService implements OrderRegister {
    private final OrderRepository orderRepository;

    @Override
    public Order register(CreateOrderSpec createOrderSpec) {
        Order order = Order.create(createOrderSpec);
        return orderRepository.save(order);
    }
}
