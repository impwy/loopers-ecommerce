package com.loopers.application.order.required;

import com.loopers.domain.order.orderitem.OrderItem;

public interface OrderItemRepository {
    OrderItem save(OrderItem orderItem);
}
