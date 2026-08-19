package com.loopers.application.order.required;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderFixture;
import com.loopers.domain.order.OrderNo;
import com.loopers.support.stereotype.ApplicationJpaServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationJpaServiceTest
@RequiredArgsConstructor
class OrderRepositoryTest {
    final OrderRepository orderRepository;
    final EntityManager entityManager;

    @Test
    void saveAndFindById() {
        Order order = orderRepository.save(createOrderWithItem());
        entityManager.flush();
        entityManager.clear();

        Order found = orderRepository.findById(order.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(order.getId());
        assertThat(found.getMemberId()).isEqualTo(1L);
    }

    @Test
    void findByMemberId() {
        Order order = orderRepository.save(createOrderWithItem());
        entityManager.flush();
        entityManager.clear();

        Order found = orderRepository.findByMemberId(1L).orElseThrow();

        assertThat(found.getId()).isEqualTo(order.getId());
    }

    @Test
    void findWithOrderItem() {
        Order order = orderRepository.save(createOrderWithItem());
        entityManager.flush();
        entityManager.clear();

        List<Order> found = orderRepository.findWithOrderItem(1L);

        assertThat(found).singleElement().satisfies(item -> assertThat(item.getOrderItems()).hasSize(1));
    }

    @Test
    void findByOrderNo() {
        Order order = orderRepository.save(createOrderWithItem());
        String orderNo = order.getOrderNo().value();
        entityManager.flush();
        entityManager.clear();

        Order found = orderRepository.findByOrderNo(new OrderNo(orderNo)).orElseThrow();

        assertThat(found.getId()).isEqualTo(order.getId());
    }

    @Test
    void findByOrderNoWithItems() {
        Order order = orderRepository.save(createOrderWithItem());
        String orderNo = order.getOrderNo().value();
        entityManager.flush();
        entityManager.clear();

        Order found = orderRepository.findByOrderNoWithItems(new OrderNo(orderNo)).orElseThrow();

        assertThat(found.getId()).isEqualTo(order.getId());
        assertThat(found.getOrderItems()).hasSize(1);
    }

    private Order createOrderWithItem() {
        return OrderFixture.createOrder().createOrderItems(List.of(
                new com.loopers.domain.order.orderitem.CreateOrderItemSpec(1L, 2L, null)
        ));
    }
}
