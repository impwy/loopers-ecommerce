package com.loopers.application.required;

import com.loopers.domain.order.Order;

public interface OrderRepository {
    Order save(Order order);
}
