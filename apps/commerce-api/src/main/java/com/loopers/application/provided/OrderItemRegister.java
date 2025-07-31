package com.loopers.application.provided;

import com.loopers.domain.orderitem.CreateOrderItemSpec;
import com.loopers.domain.orderitem.OrderItem;

public interface OrderItemRegister {
    OrderItem register(CreateOrderItemSpec createOrderItemSpec);
}
