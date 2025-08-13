package com.loopers.application.order;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.provided.OrderRegister;
import com.loopers.application.required.MemberRepository;
import com.loopers.application.required.ProductRepository;
import com.loopers.domain.order.CreateOrderSpec;
import com.loopers.domain.order.orderitem.CreateOrderItemSpec;
import com.loopers.support.error.CoreException;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest
class OrderRegisterIntegrationTest {

    @Autowired
    private OrderRegister orderRegister;

    @MockitoSpyBean
    private MemberRepository memberRepository;

    @MockitoSpyBean
    private ProductRepository productRepository;

    @Autowired
    DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("존재하지 않는 유저 주문 시 실패")
    @Test
    void create_order_fail_when_user_not_existed() {
        CreateOrderSpec createOrderSpec = CreateOrderSpec.of(null);
        CreateOrderItemSpec createOrderItemSpec = CreateOrderItemSpec.of(1L, 10L);

        assertThatThrownBy(() -> orderRegister.createOrder(createOrderSpec, List.of(createOrderItemSpec)))
                .isInstanceOf(CoreException.class);
    }
}
