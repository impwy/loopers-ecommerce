package com.loopers.application.order.required;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderFixture;
import com.loopers.domain.order.orderitem.CreateOrderItemSpec;
import com.loopers.domain.order.orderitem.OrderItem;
import com.loopers.support.BaseRepositoryTest;
import com.loopers.support.stereotype.ApplicationJpaServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationJpaServiceTest
@RequiredArgsConstructor
class OrderItemRepositoryTest extends BaseRepositoryTest {
    final OrderItemRepository orderItemRepository;

    @Test
    void save() {
        Order order = prepareOrder();
        CreateOrderItemSpec itemSpec = OrderFixture.createOrderItemSpec(1L, 2L, null);
        OrderItem orderItem = orderItemRepository.save(
                OrderItem.create(order, itemSpec.productId(), itemSpec.quantity(), itemSpec.couponId()));
        flushAndClear();

        assertThat(orderItem.getId()).isNotNull();
        assertThat(orderItem.getProductId()).isEqualTo(1L);
        assertThat(orderItem.getOrder().getId()).isEqualTo(order.getId());
    }
}
