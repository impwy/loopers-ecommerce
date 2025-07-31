package com.loopers.application.provided;

import com.loopers.domain.order.Order;

public interface OrderFinder {
    Order find(Long orderId);

    Order findByMemberId(Long memberId);
}
