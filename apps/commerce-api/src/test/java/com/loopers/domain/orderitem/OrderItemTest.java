package com.loopers.domain.orderitem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.loopers.domain.order.OrderFixture;
import com.loopers.domain.order.orderitem.OrderItem;

class OrderItemTest {

    @DisplayName("유저가 없으면 주문 생성 실패")
    @Test
    void create_order_fail_when_memberId_is_null() {
        IllegalArgumentException illegalArgumentException
                = assertThrows(IllegalArgumentException.class, () -> OrderItem.create(null, 1L, 1L));

        assertThat(illegalArgumentException.getMessage()).isEqualTo("잘못된 주문입니다.");
    }

    @DisplayName("상품이 없으면 주문 생성 실패")
    @Test
    void create_order_fail_when_productId_is_null() {
        IllegalArgumentException illegalArgumentException
                = assertThrows(IllegalArgumentException.class, () -> OrderItem.create(OrderFixture.createOrder(), null, 1L));

        assertThat(illegalArgumentException.getMessage()).isEqualTo("상품을 찾을 수 없습니다.");
    }

    @DisplayName("수량이 잘못되면 주문 생성 실패")
    @Test
    void create_order_fail_when_quantity_is_invalid() {
        IllegalArgumentException illegalArgumentException
                = assertThrows(IllegalArgumentException.class, () -> OrderItem.create(OrderFixture.createOrder(), 1L, -1L));

        assertThat(illegalArgumentException.getMessage()).isEqualTo("잘못된 수량입니다. : -1");
    }

    @DisplayName("수량이 잘못되면 주문 생성 실패")
    @Test
    void create_order_fail_when_quantity_is_null() {
        IllegalArgumentException illegalArgumentException
                = assertThrows(IllegalArgumentException.class, () -> OrderItem.create(OrderFixture.createOrder(), 1L, null));

        assertThat(illegalArgumentException.getMessage()).isEqualTo("잘못된 수량입니다. : null");
    }

    @DisplayName("주문 생성 성공")
    @Test
    void create_order_test() {
        OrderItem orderItem = OrderItem.create(OrderFixture.createOrder(), 1L, 1L);

        assertAll(
                () -> assertThat(orderItem.getProductId()).isEqualTo(1L),
                () -> assertThat(orderItem.getQuantity()).isEqualTo(1L)
        );
    }
}
