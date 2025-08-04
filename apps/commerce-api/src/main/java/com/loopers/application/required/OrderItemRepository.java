package com.loopers.application.required;

import com.loopers.domain.orderitem.OrderItem;

public interface OrderItemRepository {
    OrderItem save(OrderItem orderItem);
}
