package com.loopers.application.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.loopers.domain.orderitem.CreateOrderItemSpec;
import com.loopers.domain.orderitem.OrderItem;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest
class OrderItemRegisterIntegrationTest {

    @Autowired
    private OrderItemRegister orderItemRegister;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("잘못된 주문 번호로 주문 생성 시 NOT_FOUND 반환")
    @Test
    void create_orderItem_fail_when_orderId_is_invalid() {
        CreateOrderItemSpec createOrderItemSpec = CreateOrderItemSpec.of(-1L, 1L, 100L, BigDecimal.TEN);
        CoreException coreException = Assertions.assertThrows(CoreException.class, () -> orderItemRegister.register(createOrderItemSpec));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }

    @DisplayName("잘못된 상품 번호로 주문 생성 시 NOT_FOUND 반환")
    @Test
    void create_orderItem_fail_when_productId_is_invalid() {
        CreateOrderItemSpec createOrderItemSpec = CreateOrderItemSpec.of(1L, -1L, 100L, BigDecimal.TEN);
        CoreException coreException = Assertions.assertThrows(CoreException.class, () -> orderItemRegister.register(createOrderItemSpec));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }

    @DisplayName("잘못된 수량으로 주문 생성 시 BAD_REQUEST 반환")
    @Test
    void create_orderItem_fail_when_quantity_is_invalid() {
        CreateOrderItemSpec createOrderItemSpec = CreateOrderItemSpec.of(1L, 1L, -100L, BigDecimal.TEN);
        CoreException coreException = Assertions.assertThrows(CoreException.class, () -> orderItemRegister.register(createOrderItemSpec));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }

    @DisplayName("잘못된 총금액으로 주문 생성 시 BAD_REQUEST 반환")
    @Test
    void create_orderItem_fail_when_price_is_invalid() {
        CreateOrderItemSpec createOrderItemSpec = CreateOrderItemSpec.of(1L, 1L, 100L, BigDecimal.valueOf(-100));
        CoreException coreException = Assertions.assertThrows(CoreException.class, () -> orderItemRegister.register(createOrderItemSpec));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }

    @DisplayName("주문 생성 성공")
    @Test
    void create_orderItem_test() {
        CreateOrderItemSpec createOrderItemSpec = CreateOrderItemSpec.of(1L, 1L, 100L, BigDecimal.valueOf(100));

        OrderItem expected = orderItemRegister.register(createOrderItemSpec);

        assertAll(
                () -> assertThat(expected.getOrderId()).isEqualTo(createOrderItemSpec.orderId()),
                () -> assertThat(expected.getProductId()).isEqualTo(createOrderItemSpec.productId()),
                () -> assertThat(expected.getQuantity()).isEqualTo(createOrderItemSpec.quantity()),
                () -> assertThat(expected.getPrice()).isEqualTo(createOrderItemSpec.price())
        );
    }
}
