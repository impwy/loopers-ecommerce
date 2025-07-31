package com.loopers.application;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.OrderItemRegister;
import com.loopers.application.required.OrderItemRepository;
import com.loopers.domain.orderitem.CreateOrderItemSpec;
import com.loopers.domain.orderitem.OrderItem;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderItemModifyService implements OrderItemRegister {
    private final OrderItemRepository orderItemRepository;

    @Override
    public OrderItem register(CreateOrderItemSpec createOrderItemSpec) {
        OrderItem orderItem = OrderItem.create(createOrderItemSpec);
        return orderItemRepository.save(orderItem);
    }
}
