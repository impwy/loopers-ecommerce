package com.loopers.application.order.required;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderFixture;
import com.loopers.domain.order.OrderNo;
import com.loopers.support.BaseRepositoryTest;
import com.loopers.support.stereotype.ApplicationJpaServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationJpaServiceTest
@RequiredArgsConstructor
class OrderRepositoryTest extends BaseRepositoryTest {
    final OrderRepository orderRepository;

    @Test
    void saveAndFindById() {
        Order order = orderRepository.save(createOrderWithItem());
        flushAndClear();

        Order found = orderRepository.findById(order.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(order.getId());
        assertThat(found.getMemberId()).isEqualTo(1L);
    }

    @Test
    void findByMemberId() {
        Order order = orderRepository.save(createOrderWithItem());
        flushAndClear();

        Order found = orderRepository.findByMemberId(1L).orElseThrow();

        assertThat(found.getId()).isEqualTo(order.getId());
    }

    @Test
    void findWithOrderItem() {
        Order order = orderRepository.save(createOrderWithItem());
        flushAndClear();

        List<Order> found = orderRepository.findWithOrderItem(1L);

        assertThat(found).singleElement().satisfies(item -> assertThat(item.getOrderItems()).hasSize(1));
    }

    @Test
    void findByOrderNo() {
        Order order = orderRepository.save(createOrderWithItem());
        String orderNo = order.getOrderNo().value();
        flushAndClear();

        Order found = orderRepository.findByOrderNo(new OrderNo(orderNo)).orElseThrow();

        assertThat(found.getId()).isEqualTo(order.getId());
    }

    @Test
    void findByOrderNoWithItems() {
        Order order = orderRepository.save(createOrderWithItem());
        String orderNo = order.getOrderNo().value();
        flushAndClear();

        Order found = orderRepository.findByOrderNoWithItems(new OrderNo(orderNo)).orElseThrow();

        assertThat(found.getId()).isEqualTo(order.getId());
        assertThat(found.getOrderItems()).hasSize(1);
    }

    private Order createOrderWithItem() {
        return OrderFixture.createOrder().createOrderItems(List.of(
                OrderFixture.createOrderItemSpec(1L, 2L, null)
        ));
    }
}
