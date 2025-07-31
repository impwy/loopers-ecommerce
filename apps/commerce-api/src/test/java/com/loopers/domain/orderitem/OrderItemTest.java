package com.loopers.domain.orderitem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

class OrderItemTest {

    @DisplayName("유저가 없으면 주문 생성 실패")
    @Test
    void create_order_fail_when_memberId_is_null() {
        CoreException coreException
                = assertThrows(CoreException.class, () -> OrderItem.create(null, 1L, 1L, BigDecimal.ONE));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        assertThat(coreException.getCustomMessage()).isEqualTo("유저는 필수입니다.");
    }

    @DisplayName("상품이 없으면 주문 생성 실패")
    @Test
    void create_order_fail_when_productId_is_null() {
        CoreException coreException
                = assertThrows(CoreException.class, () -> OrderItem.create(1L, null, 1L, BigDecimal.ONE));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        assertThat(coreException.getCustomMessage()).isEqualTo("상품은 필수입니다.");
    }

    @DisplayName("수량이 잘못되면 주문 생성 실패")
    @Test
    void create_order_fail_when_quantity_is_invalid() {
        CoreException coreException
                = assertThrows(CoreException.class, () -> OrderItem.create(1L, 1L, -1L, BigDecimal.ONE));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        assertThat(coreException.getCustomMessage()).isEqualTo("잘못된 수량입니다. : -1");
    }

    @DisplayName("수량이 잘못되면 주문 생성 실패")
    @Test
    void create_order_fail_when_quantity_is_null() {
        CoreException coreException
                = assertThrows(CoreException.class, () -> OrderItem.create(1L, 1L, null, BigDecimal.ONE));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        assertThat(coreException.getCustomMessage()).isEqualTo("잘못된 수량입니다. : null");
    }

    @DisplayName("총 가격이 잘못되면 주문 생성 실패")
    @Test
    void create_order_fail_when_price_is_invalid() {
        CoreException coreException
                = assertThrows(CoreException.class, () -> OrderItem.create(1L, 1L, 1L, BigDecimal.valueOf(-1)));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        assertThat(coreException.getCustomMessage()).isEqualTo("잘못된 총 가격입니다. : -1");
    }

    @DisplayName("총 가격이 잘못되면 주문 생성 실패")
    @Test
    void create_order_fail_when_price_is_null() {
        CoreException coreException
                = assertThrows(CoreException.class, () -> OrderItem.create(1L, 1L, 1L, null));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        assertThat(coreException.getCustomMessage()).isEqualTo("잘못된 총 가격입니다. : null");
    }

    @DisplayName("주문 생성 성공")
    @Test
    void create_order_test() {
        OrderItem orderItem = OrderItem.create(1L, 1L, 1L, BigDecimal.ONE);

        assertAll(
                () -> assertThat(orderItem.getOrderId()).isEqualTo(1L),
                () -> assertThat(orderItem.getProductId()).isEqualTo(1L),
                () -> assertThat(orderItem.getQuantity()).isEqualTo(1L),
                () -> assertThat(orderItem.getPrice()).isEqualTo(BigDecimal.ONE)
        );
    }
}
