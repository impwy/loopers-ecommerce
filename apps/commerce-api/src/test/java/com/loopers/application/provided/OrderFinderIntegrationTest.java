package com.loopers.application.provided;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.required.OrderRepository;
import com.loopers.domain.order.CreateOrderSpec;
import com.loopers.domain.order.Order;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest
class OrderFinderIntegrationTest {

    @MockitoSpyBean
    private OrderRepository orderRepository;

    @Autowired
    private OrderFinder orderFinder;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("존재하지 않는 주문 번호 조회 시 NOT_FOUND 반환")
    @Test
    void find_order_fail_test() {
        CoreException coreException = assertThrows(CoreException.class, () -> orderFinder.find(-1L));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
    }

    @DisplayName("주문 ID로 주문 조회 성공")
    @Test
    void find_order_by_order_id() {
        Order order = orderRepository.save(Order.create(CreateOrderSpec.of(1L)));

        Order expected = orderFinder.find(order.getId());

        assertAll(
                () -> assertThat(expected.getMemberId()).isEqualTo(order.getMemberId()),
                () -> assertThat(expected.getOrderNo()).isNotNull()
        );
    }

    @DisplayName("존재하지 않는 유저로 조회 시 NOT_FOUND 반환")
    @Test
    void find_order_fail_with_memberId_test() {
        CoreException coreException = assertThrows(CoreException.class, () -> orderFinder.findByMemberId(-1L));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
    }

    @DisplayName("유저 아이디로 주문 조회 성공")
    @Test
    void find_order_by_memberId() {
        Order order = orderRepository.save(Order.create(CreateOrderSpec.of(1L)));

        Order expected = orderFinder.findByMemberId(order.getMemberId());

        assertAll(
                () -> assertThat(expected.getMemberId()).isEqualTo(order.getMemberId()),
                () -> assertThat(expected.getOrderNo()).isNotNull()
        );
    }

    @DisplayName("주문 조회 시 주문 아이템도 조회")
    @Test
    void find_order_with_order_item() {
        Order firstOrder = Order.create(CreateOrderSpec.of(1L));
        firstOrder.addOrderItem(1L, 10L, BigDecimal.TEN);
        firstOrder.addOrderItem(2L, 20L, BigDecimal.TEN);
        firstOrder.addOrderItem(3L, 30L, BigDecimal.TEN);
        orderRepository.save(firstOrder);

        Order secondOrder = Order.create(CreateOrderSpec.of(1L));
        secondOrder.addOrderItem(1L, 10L, BigDecimal.TEN);
        secondOrder.addOrderItem(2L, 20L, BigDecimal.TEN);
        secondOrder.addOrderItem(3L, 30L, BigDecimal.TEN);
        orderRepository.save(secondOrder);

        List<Order> orders = orderFinder.findWithOrderItem(1L);

        assertAll(
                () -> assertThat(orders.size()).isEqualTo(2),
                () -> assertThat(orders.getFirst().getOrderItems().size()).isEqualTo(3),
                () -> assertThat(orders.getFirst().getOrderItems().getFirst().getQuantity()).isEqualTo(10L)
        );
    }
}
