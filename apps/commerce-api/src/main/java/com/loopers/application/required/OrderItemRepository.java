package com.loopers.application.required;

import com.loopers.domain.order.orderitem.OrderItem;

public interface OrderItemRepository {
    OrderItem save(OrderItem orderItem);
}
