package com.loopers.application.required;

import java.util.Optional;

import com.loopers.domain.order.Order;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> find(Long orderId);

    Optional<Order> findByMemberId(Long memberId);
}
