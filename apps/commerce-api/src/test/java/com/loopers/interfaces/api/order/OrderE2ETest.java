package com.loopers.interfaces.api.order;

import com.loopers.application.required.*;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponType;
import com.loopers.domain.coupon.CreateCouponSpec;
import com.loopers.domain.coupon.DiscountPolicy;
import com.loopers.domain.inventory.Inventory;
import com.loopers.domain.member.CreateMemberSpec;
import com.loopers.domain.member.Gender;
import com.loopers.domain.member.Member;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderStatus;
import com.loopers.domain.product.Product;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.order.dto.OrderV1Dto;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderE2ETest {

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

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Test
    @DisplayName("주문 생성 E2E 테스트")
    void createOrderE2ETest() {
        // given
        Member member = memberRepository.save(Member.create(new CreateMemberSpec("testuser1", "password", Gender.MALE, "test@example.com", LocalDate.now())));
        Brand brand = brandRepository.create(Brand.create("Test Brand", "Brand Description"));
        Product product = productRepository.save(Product.create("Test Product", "Product Description", BigDecimal.valueOf(10000), brand, ZonedDateTime.now()));
        inventoryRepository.save(Inventory.create(product.getId(), 100L));
        Coupon coupon = couponRepository.create(Coupon.create(CreateCouponSpec.create("AMOUNT_1000", 100L, DiscountPolicy.AMOUNT, CouponType.ORDER)));

        OrderV1Dto.Request.CreateOrderRequest orderRequest = new OrderV1Dto.Request.CreateOrderRequest(product.getId(), 2L);
        OrderV1Dto.Request.CreateOrderWithCouponRequest request = new OrderV1Dto.Request.CreateOrderWithCouponRequest(List.of(orderRequest), coupon.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-USER-ID", member.getMemberId().memberId());
        HttpEntity<OrderV1Dto.Request.CreateOrderWithCouponRequest> httpEntity = new HttpEntity<>(request, headers);

        // when
        ResponseEntity<ApiResponse<List<OrderV1Dto.Response.OrderInfo>>> response = testRestTemplate.exchange(
                "/api/v1/orders",
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<>() {}
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String orderNo = response.getBody().data().get(0).orderNo();
        Order order = orderRepository.findByOrderNoWithItems(orderNo).get();

        assertThat(order.getMemberId()).isEqualTo(member.getId());
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(order.getOrderItems().get(0).getProductId()).isEqualTo(product.getId());
        assertThat(order.getOrderItems().get(0).getQuantity()).isEqualTo(2L);
        assertThat(order.getOrderItems().get(0).getCouponId()).isEqualTo(coupon.getId());
    }
}
