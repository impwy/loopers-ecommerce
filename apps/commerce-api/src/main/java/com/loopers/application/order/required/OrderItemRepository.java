package com.loopers.application.order.required;

import org.springframework.data.repository.Repository;

import com.loopers.domain.order.orderitem.OrderItem;

public interface OrderItemRepository extends Repository<OrderItem, Long> {
    OrderItem save(OrderItem orderItem);
}
