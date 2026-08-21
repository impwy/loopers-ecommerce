package com.loopers.adapter.webapi.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.client.EntityExchangeResult;

import com.loopers.adapter.integration.feign.PgFeignClient;
import com.loopers.adapter.webapi.ApiResponse;
import com.loopers.adapter.webapi.payment.dto.PaymentV1Dto;
import com.loopers.application.order.required.OrderRepository;
import com.loopers.application.payment.PaymentRequest;
import com.loopers.application.payment.required.PaymentRepository;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponType;
import com.loopers.domain.coupon.CouponFixture;
import com.loopers.domain.coupon.DiscountPolicy;
import com.loopers.domain.inventory.Inventory;
import com.loopers.domain.member.Member;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderFixture;
import com.loopers.domain.order.OrderStatus;
import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.PaymentFixture;
import com.loopers.domain.payment.PaymentStatus;
import com.loopers.domain.payment.PaymentType;
import com.loopers.domain.payment.Payments;
import com.loopers.domain.product.Product;
import com.loopers.support.BaseApiTest;
import com.loopers.support.stereotype.WebApiAdapterTest;

import lombok.RequiredArgsConstructor;

@WebApiAdapterTest
@RequiredArgsConstructor
class PaymentE2ETest extends BaseApiTest {
    final OrderRepository orderRepository;
    final PaymentRepository paymentRepository;

    @MockitoSpyBean
    private PgFeignClient pgFeignClient;

    Member member;
    Product product;
    Coupon coupon;
    Order order;

    @BeforeEach
    void setUp() {
        member = prepareMember();
        var brand = prepareBrand("Test Brand", "Brand Description");
        product = prepareProduct(brand, "Test Product", "Product Description",
                                 BigDecimal.valueOf(10000), ZonedDateTime.parse("2025-01-01T00:00:00Z"));
        prepareInventory(product, 100L);
        coupon = prepareCoupon(CouponFixture.createCouponSpec("AMOUNT_1000", 100L,
                                                       DiscountPolicy.AMOUNT, CouponType.ORDER), member);
        Order newOrder = OrderFixture.createOrder(member.getId());
        newOrder.createOrderItems(List.of(OrderFixture.createOrderItemSpec(product.getId(), 2L, coupon.getId())));
        order = orderRepository.save(newOrder);
    }

    @Test
    @DisplayName("결제 E2E 테스트")
    void paymentE2ETest() {
        String transactionKey = "test_transaction_key";
        PaymentRequest request = PaymentFixture.createPaymentRequest(order.getId(), "1234-1234-1234-1234",
                                                                      CardType.SAMSUNG, BigDecimal.valueOf(19000),
                                                                      PaymentType.CARD);

        doReturn(ApiResponse.success(new PaymentV1Dto.TransactionResponse(transactionKey,
                PaymentStatus.PENDING, null))).when(pgFeignClient).requestPayment(anyString(), any());

        EntityExchangeResult<Void> paymentResult = restTestClient.post()
                .uri("/api/v1/payments/pay")
                .header("X-USER-ID", member.getUserId().userId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class)
                .returnResult();
        assertThat(paymentResult).isNotNull();

        PaymentV1Dto.TransactionDetailResponse transactionDetail =
                new PaymentV1Dto.TransactionDetailResponse(transactionKey, order.getOrderNo().value(),
                        CardType.SAMSUNG, "1234-1234-1234-1234", BigDecimal.valueOf(19000), PaymentStatus.SUCCESS, null);
        doReturn(ApiResponse.success(transactionDetail)).when(pgFeignClient).getPaymentStatus(anyString(), anyString());

        PaymentV1Dto.TransactionResponse callback =
                new PaymentV1Dto.TransactionResponse(transactionKey, PaymentStatus.SUCCESS, null);
        EntityExchangeResult<Void> callbackResult = restTestClient.post()
                .uri("/api/v1/payments/pg-callback")
                .header("X-USER-ID", member.getUserId().userId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(callback)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class)
                .returnResult();
        assertThat(callbackResult).isNotNull();

        await().atMost(3, TimeUnit.SECONDS).untilAsserted(
                () -> {
                    Order finishedOrder = orderRepository.findById(order.getId()).orElseThrow();
                    assertThat(finishedOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);

                    List<Payments> payments = paymentRepository.findAllByOrderId(order.getOrderNo().value());
                    assertThat(payments).hasSize(1);
                    assertThat(payments.get(0).getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);

                    Inventory inventory = inventoryRepository.findByProductId(product.getId()).orElseThrow();
                    assertThat(inventory.getQuantity()).isEqualTo(100L);

                    Coupon updatedCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
                    assertThat(updatedCoupon.getQuantity()).isEqualTo(100L);
                }
        );
    }
}
