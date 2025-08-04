package com.loopers.application.provided;

import com.loopers.domain.order.CreateOrderSpec;
import com.loopers.domain.order.Order;

public interface OrderRegister {
    Order register(CreateOrderSpec createOrderSpec);
}
