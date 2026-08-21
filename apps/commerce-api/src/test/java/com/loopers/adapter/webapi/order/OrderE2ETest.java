package com.loopers.adapter.webapi.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.EntityExchangeResult;

import com.loopers.adapter.webapi.ApiResponse;
import com.loopers.adapter.webapi.order.dto.OrderV1Dto;
import com.loopers.application.order.required.OrderRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponType;
import com.loopers.domain.coupon.CouponFixture;
import com.loopers.domain.coupon.DiscountPolicy;
import com.loopers.domain.member.Member;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderFixture;
import com.loopers.domain.order.OrderNo;
import com.loopers.domain.order.OrderStatus;
import com.loopers.domain.product.Product;
import com.loopers.support.BaseApiTest;
import com.loopers.support.stereotype.WebApiAdapterTest;

@WebApiAdapterTest
class OrderE2ETest extends BaseApiTest {
    private final OrderRepository orderRepository;

    OrderE2ETest(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Test
    @DisplayName("주문 생성 E2E 테스트")
    void createOrderE2ETest() {
        Member member = prepareMember();
        Brand brand = prepareBrand("Test Brand", "Brand Description");
        Product product = prepareProduct(brand, "Test Product", "Product Description",
                BigDecimal.valueOf(10000), ZonedDateTime.parse("2025-01-01T00:00:00Z"));
        prepareInventory(product, 100L);
        Coupon coupon = prepareCoupon(CouponFixture.createCouponSpec("AMOUNT_1000", 100L,
                DiscountPolicy.AMOUNT, CouponType.ORDER));

        var orderRequest = OrderFixture.createOrderRequest(product.getId(), 2L);
        var request = OrderFixture.createOrderWithCouponRequest(List.of(orderRequest), coupon.getId());

        EntityExchangeResult<ApiResponse<List<OrderV1Dto.OrderInfo>>> result = restTestClient.post()
                .uri("/api/v1/orders")
                .header("X-USER-ID", member.getUserId().userId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiResponse<List<OrderV1Dto.OrderInfo>>>() {})
                .returnResult();
        ApiResponse<List<OrderV1Dto.OrderInfo>> response = result.getResponseBody();
        assertThat(response).isNotNull();
        assertThat(response.data()).isNotNull();
        assertThat(response.meta().result()).isEqualTo(ApiResponse.Metadata.Result.SUCCESS);

        OrderV1Dto.OrderInfo orderInfo = response.data().getFirst();
        assertThat(orderInfo.productName()).isEqualTo(product.getName());
        assertThat(orderInfo.totalQuantity()).isEqualTo(2L);
        assertThat(orderInfo.totalPrice()).isEqualByComparingTo(BigDecimal.valueOf(20000));


            Order order = orderRepository.findByOrderNoWithItems(new OrderNo(orderInfo.orderNo())).orElseThrow();
            assertThat(order.getMemberId()).isEqualTo(member.getId());
            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(order.getOrderItems()).hasSize(1);
            assertThat(order.getOrderItems().get(0).getProductId()).isEqualTo(product.getId());
            assertThat(order.getOrderItems().get(0).getQuantity()).isEqualTo(2L);
            assertThat(order.getOrderItems().get(0).getCouponId()).isEqualTo(coupon.getId());
            assertThat(inventoryRepository.findByProductId(product.getId()).orElseThrow().getQuantity()).isEqualTo(98L);
    }
}
