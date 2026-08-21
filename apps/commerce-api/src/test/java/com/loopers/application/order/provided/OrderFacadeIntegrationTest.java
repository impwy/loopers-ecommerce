package com.loopers.application.order.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.application.order.OrderFacade;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.member.Member;
import com.loopers.domain.product.Product;
import com.loopers.application.order.CreateOrderRequest;
import com.loopers.application.order.CreateOrderWithCouponRequest;
import com.loopers.application.order.OrderInfo;
import com.loopers.shared.error.CoreException;
import com.loopers.shared.error.ErrorType;
import com.loopers.support.BaseApplicationServiceTest;
import com.loopers.support.stereotype.ApplicationServiceTest;

@ApplicationServiceTest
class OrderFacadeIntegrationTest extends BaseApplicationServiceTest {

    @Autowired
    private OrderFacade orderFacade;

    Member savedMember;
    Product savedProduct;
    Coupon savedCoupon;

    @BeforeEach
    void setUp() {
        savedMember = prepareMember();
        savedProduct = prepareProduct(prepareBrand());
        prepareInventory(savedProduct, 100L);
        savedCoupon = prepareCoupon();
    }

    @DisplayName("주문 실패 테스트 : 존재하지 않는 상품")
    @Test
    void create_order_fail_when_product_not_existed() {
        CreateOrderRequest createOrderRequest = CreateOrderRequest.of(-1L, 100L);
        CoreException coreException = assertThrows(CoreException.class,
                                                   () -> orderFacade.order(savedMember.getUserId(),
                                                                           CreateOrderWithCouponRequest.create(List.of(createOrderRequest), savedMember.getId())));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        assertThat(coreException.getCustomMessage()).isEqualTo("상품을 찾을 수 없습니다.");
    }

    @DisplayName("주문 성공 테스트")
    @Transactional
    @Test
    void create_order_test() {
        CreateOrderRequest createOrderRequest = CreateOrderRequest.of(savedProduct.getId(), 10L);
        savedMember.charge(savedProduct.getPrice().multiply(BigDecimal.TEN));

        List<OrderInfo> orderProductInfos = orderFacade.order(savedMember.getUserId(),
                                                              CreateOrderWithCouponRequest.create(List.of(createOrderRequest), savedCoupon.getId()))
                                                       .orderInfos();

        BigDecimal totalPrice = savedProduct.getPrice().multiply(BigDecimal.valueOf(createOrderRequest.quantity()));

        assertAll(
                () -> assertThat(orderProductInfos.get(0).productName()).isEqualTo(savedProduct.getName()),
                () -> assertThat(orderProductInfos.get(0).totalQuantity()).isEqualTo(createOrderRequest.quantity()),
                () -> assertThat(orderProductInfos.get(0).totalPrice().compareTo(totalPrice)).isZero()
        );
    }
}
