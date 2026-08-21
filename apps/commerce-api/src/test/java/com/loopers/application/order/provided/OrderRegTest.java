package com.loopers.application.order.provided;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.loopers.domain.order.CreateOrderSpec;
import com.loopers.domain.order.orderitem.CreateOrderItemSpec;
import com.loopers.support.BaseApplicationServiceTest;
import com.loopers.shared.error.CoreException;
import com.loopers.support.stereotype.ApplicationServiceTest;

@ApplicationServiceTest
class OrderRegTest extends BaseApplicationServiceTest {

    @Autowired
    private OrderRegister orderRegister;


    @DisplayName("존재하지 않는 유저 주문 시 실패")
    @Test
    void create_order_fail_when_user_not_existed() {
        CreateOrderSpec createOrderSpec = CreateOrderSpec.of(null);
        CreateOrderItemSpec createOrderItemSpec = CreateOrderItemSpec.of(1L, 10L, 1L);

        assertThatThrownBy(() -> orderRegister.createOrder(createOrderSpec, List.of(createOrderItemSpec)))
                .isInstanceOf(CoreException.class);
    }
}
