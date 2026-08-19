package com.loopers.application.order.required;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderFixture;
import com.loopers.domain.order.orderitem.OrderItem;
import com.loopers.support.stereotype.ApplicationJpaServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationJpaServiceTest
@RequiredArgsConstructor
class OrderItemRepositoryTest {
    final OrderItemRepository orderItemRepository;
    final EntityManager entityManager;

    @Test
    void save() {
        Order order = OrderFixture.createOrder();
        entityManager.persist(order);
        OrderItem orderItem = orderItemRepository.save(OrderItem.create(order, 1L, 2L, null));
        entityManager.flush();

        assertThat(orderItem.getId()).isNotNull();
        assertThat(orderItem.getProductId()).isEqualTo(1L);
        assertThat(orderItem.getOrder().getId()).isEqualTo(order.getId());
    }
}
