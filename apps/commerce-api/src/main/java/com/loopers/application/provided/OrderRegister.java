package com.loopers.application.provided;

import java.util.List;

import com.loopers.domain.order.CreateOrderSpec;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.orderitem.CreateOrderItemSpec;

public interface OrderRegister {
    Order createOrder(CreateOrderSpec createOrderSpec, List<CreateOrderItemSpec> createOrderItemSpecs);

    void successOrder(String orderId);

    void failOrder(String orderId);
}
