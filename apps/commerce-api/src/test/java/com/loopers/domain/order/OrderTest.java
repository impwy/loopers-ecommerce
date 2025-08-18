package com.loopers.domain.order;

import static com.loopers.domain.order.Order.create;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.loopers.support.error.CoreException;

class OrderTest {

    @DisplayName("주문 생성 시 사용자가 NULL이면 실패")
    @Test
    void create_order_fail_when_user_null_test() {
        CreateOrderSpec createOrderSpec
                = CreateOrderSpec.of(null);

        assertThatThrownBy(() -> create(createOrderSpec))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 생성 성공")
    @Test
    void create_order_test() {
        CreateOrderSpec createOrderSpec
                = CreateOrderSpec.of(1L);

        Order order = create(createOrderSpec);

        assertAll(
                () -> assertThat(order.getMemberId()).isEqualTo(createOrderSpec.memberId()),
                () -> assertThat(order.getOrderNo()).isNotNull()
        );
    }
}
