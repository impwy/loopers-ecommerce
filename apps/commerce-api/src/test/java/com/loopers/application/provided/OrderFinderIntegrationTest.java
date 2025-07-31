package com.loopers.application.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.required.OrderRepository;
import com.loopers.domain.order.Address;
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
        Order order = orderRepository.save(Order.create(CreateOrderSpec.of(1L, "000000")));

        Order expected = orderFinder.find(order.getId());

        assertAll(
                () -> assertThat(expected.getMemberId()).isEqualTo(order.getMemberId()),
                () -> assertThat(expected.getOrderNo()).isEqualTo(order.getOrderNo())
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
        Order order = orderRepository.save(Order.create(CreateOrderSpec.of(1L, "000000")));

        Order expected = orderFinder.findByMemberId(order.getMemberId());

        assertAll(
                () -> assertThat(expected.getMemberId()).isEqualTo(order.getMemberId()),
                () -> assertThat(expected.getOrderNo()).isEqualTo(order.getOrderNo())
        );
    }
}
