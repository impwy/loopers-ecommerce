package com.loopers.application.order;

import java.util.List;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.OrderRegister;
import com.loopers.application.required.OrderRepository;
import com.loopers.domain.order.CreateOrderSpec;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.orderitem.CreateOrderItemSpec;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderModifyService implements OrderRegister {
    private final OrderRepository orderRepository;

    @Override
    public Order createOrder(CreateOrderSpec createOrderSpec, List<CreateOrderItemSpec> createOrderItemSpecs) {
        try {
            Order order = Order.create(createOrderSpec);
            order.createOrderItems(createOrderItemSpecs);
            return orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            throw new CoreException(ErrorType.BAD_REQUEST, e.getMessage());
        }
    }
}
