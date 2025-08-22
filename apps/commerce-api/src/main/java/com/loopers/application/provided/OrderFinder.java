package com.loopers.application.provided;

import java.util.List;

import com.loopers.domain.order.Order;

public interface OrderFinder {
    Order find(Long orderId);

    Order findByMemberId(Long memberId);

    List<Order> findWithOrderItem(Long memberId);

    Order findByOrderNo(String orderNo);
}
