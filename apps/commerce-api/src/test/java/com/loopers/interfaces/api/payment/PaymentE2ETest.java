package com.loopers.interfaces.api.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.required.BrandRepository;
import com.loopers.application.required.CouponRepository;
import com.loopers.application.required.InventoryRepository;
import com.loopers.application.required.MemberRepository;
import com.loopers.application.required.OrderRepository;
import com.loopers.application.required.PaymentRepository;
import com.loopers.application.required.ProductRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponType;
import com.loopers.domain.coupon.CreateCouponSpec;
import com.loopers.domain.coupon.DiscountPolicy;
import com.loopers.domain.coupon.membercoupon.MemberCoupon;
import com.loopers.domain.inventory.Inventory;
import com.loopers.domain.member.CreateMemberSpec;
import com.loopers.domain.member.Gender;
import com.loopers.domain.member.Member;
import com.loopers.domain.order.CreateOrderSpec;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderStatus;
import com.loopers.domain.order.orderitem.CreateOrderItemSpec;
import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.PaymentStatus;
import com.loopers.domain.payment.PaymentType;
import com.loopers.domain.payment.Payments;
import com.loopers.domain.product.Product;
import com.loopers.infrastructure.payment.feign.PgFeignClient;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentE2ETest {

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @MockitoSpyBean
    private PgFeignClient pgFeignClient;

    private Member member;
    private Product product;
    private Coupon coupon;
    private Order order;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(Member.create(new CreateMemberSpec("testuser1", "password", Gender.MALE, "test@example.com", LocalDate.now())));
        Brand brand = brandRepository.create(Brand.create("Test Brand", "Brand Description"));
        product = productRepository.save(Product.create("Test Product", "Product Description", BigDecimal.valueOf(10000), brand, ZonedDateTime.now()));
        inventoryRepository.save(Inventory.create(product.getId(), 100L));
        coupon = couponRepository.create(Coupon.create(CreateCouponSpec.create("AMOUNT_1000", 100L, DiscountPolicy.AMOUNT, CouponType.ORDER)));
        coupon.addMemberCoupon(MemberCoupon.create(member, coupon));
        Order newOrder = Order.create(CreateOrderSpec.of(member.getId()));
        newOrder.createOrderItems(List.of(CreateOrderItemSpec.of(product.getId(), 2L, coupon.getId())));
        order = orderRepository.save(newOrder);
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Test
    @DisplayName("결제 E2E 테스트")
    void paymentE2ETest() {
        String transactionKey = "test_transaction_key";
        PaymentV1Dto.Request.PaymentRequest request = new PaymentV1Dto.Request.PaymentRequest(order.getId(), "1234-1234-1234-1234", CardType.SAMSUNG, BigDecimal.valueOf(19000), PaymentType.CARD);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-USER-ID", member.getMemberId().memberId());
        HttpEntity<PaymentV1Dto.Request.PaymentRequest> httpEntity = new HttpEntity<>(request, headers);

        doReturn(ApiResponse.success(new PaymentV1Dto.Response.TransactionResponse(transactionKey, PaymentStatus.PENDING, null)))
                .when(pgFeignClient).requestPayment(anyString(), any());

        PaymentV1Dto.Response.TransactionDetailResponse transactionDetail = new PaymentV1Dto.Response.TransactionDetailResponse(transactionKey, order.getOrderNo().value(), CardType.SAMSUNG, "1234-1234-1234-1234", BigDecimal.valueOf(19000), PaymentStatus.SUCCESS, null);
        doReturn(ApiResponse.success(transactionDetail))
                .when(pgFeignClient).getPaymentStatus(anyString(), anyString());

        testRestTemplate.exchange("/api/v1/payments/pay", HttpMethod.POST, httpEntity, Void.class);

        HttpEntity<PaymentV1Dto.Response.TransactionResponse> callbackHttpEntity = new HttpEntity<>(new PaymentV1Dto.Response.TransactionResponse(transactionKey, PaymentStatus.SUCCESS, null), headers);
        testRestTemplate.exchange("/api/v1/payments/pg-callback", HttpMethod.POST, callbackHttpEntity, Void.class);

        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            Order finishedOrder = orderRepository.find(order.getId()).get();
            assertThat(finishedOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);

            List<Payments> payments = paymentRepository.findALlByOrderId(order.getOrderNo().value());
            assertThat(payments).hasSize(1);
            assertThat(payments.get(0).getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);

            Inventory inventory = inventoryRepository.findByProductId(product.getId()).get();
            assertThat(inventory.getQuantity()).isEqualTo(100L);

            Coupon updatedCoupon = couponRepository.find(coupon.getId()).get();
            assertThat(updatedCoupon.getQuantity()).isEqualTo(100L);
        });
    }
}
