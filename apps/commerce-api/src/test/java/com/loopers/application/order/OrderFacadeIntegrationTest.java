package com.loopers.application.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.application.required.BrandRepository;
import com.loopers.application.required.CouponRepository;
import com.loopers.application.required.InventoryRepository;
import com.loopers.application.required.MemberRepository;
import com.loopers.application.required.ProductRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandFixture;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponFixture;
import com.loopers.domain.inventory.Inventory;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductFixture;
import com.loopers.interfaces.api.order.dto.OrderV1Dto.Request.CreateOrderRequest;
import com.loopers.interfaces.api.order.dto.OrderV1Dto.Request.CreateOrderWithCouponRequest;
import com.loopers.interfaces.api.order.dto.OrderV1Dto.Response.OrderInfo;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest
class OrderFacadeIntegrationTest {

    @Autowired
    private OrderFacade orderFacade;

    @MockitoSpyBean
    private MemberRepository memberRepository;

    @MockitoSpyBean
    private ProductRepository productRepository;

    @MockitoSpyBean
    private BrandRepository brandRepository;

    @MockitoSpyBean
    private InventoryRepository inventoryRepository;

    @MockitoSpyBean
    private CouponRepository couponRepository;

    @Autowired
    DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    Member savedMember;
    Product savedProduct;
    Coupon savedCoupon;
    Inventory savedInventory;

    @BeforeEach
    void setUp() {
        Brand brand = brandRepository.create(BrandFixture.createBrand());

        Member member = MemberFixture.createMember();
        savedMember = memberRepository.save(member);

        Product product = ProductFixture.createProduct(brand);
        savedProduct = productRepository.save(product);

        Inventory inventory = Inventory.create(savedProduct.getId(), 100L);
        savedInventory = inventoryRepository.save(inventory);

        Coupon coupon = CouponFixture.createCoupon();
        savedCoupon = couponRepository.create(coupon);
    }

    @DisplayName("주문 실패 테스트 : 존재하지 않는 상품")
    @Test
    void create_order_fail_when_product_not_existed() {
        CreateOrderRequest createOrderRequest = CreateOrderRequest.of(-1L, 100L);
        CoreException coreException = assertThrows(CoreException.class,
                                                   () -> orderFacade.order(savedMember.getMemberId(),
                                                                           CreateOrderWithCouponRequest.create(List.of(createOrderRequest), savedMember.getId())));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        assertThat(coreException.getCustomMessage()).isEqualTo("상품을 찾을 수 없습니다.");
    }

    @DisplayName("주문 실패 테스트 : 재고 부족")
    @Test
    void create_order_fail_when_inventory_soldout() {
        CreateOrderRequest createOrderRequest = CreateOrderRequest.of(savedProduct.getId(), 101L);
        CoreException coreException = assertThrows(CoreException.class,
                                                   () -> orderFacade.order(savedMember.getMemberId(),
                                                                           CreateOrderWithCouponRequest.create(List.of(createOrderRequest), savedCoupon.getId())));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        assertThat(coreException.getCustomMessage()).isEqualTo("잔여 재고가 없습니다.");
    }

    @DisplayName("주문 실패 테스트 : 잔액 부족")
    @Test
    void create_order_fail_when_point_is_not_enough() {
        CreateOrderRequest createOrderRequest = CreateOrderRequest.of(savedProduct.getId(), 100L);
        BigDecimal totalPrice = savedProduct.getPrice().multiply(BigDecimal.valueOf(createOrderRequest.quantity()));
        savedMember.charge(totalPrice.subtract(BigDecimal.ONE));

        CoreException coreException = assertThrows(CoreException.class,
                                                   () -> orderFacade.order(savedMember.getMemberId(),
                                                                           CreateOrderWithCouponRequest.create(List.of(createOrderRequest), savedCoupon.getId())));

        assertThat(coreException.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        assertThat(coreException.getMessage()).isEqualTo("잔액이 부족합니다.");
    }

    @DisplayName("주문 성공 테스트")
    @Transactional
    @Test
    void create_order_test() {
        CreateOrderRequest createOrderRequest = CreateOrderRequest.of(savedProduct.getId(), 10L);
        savedMember.charge(savedProduct.getPrice().multiply(BigDecimal.TEN));

        List<OrderInfo> orderProductInfos = orderFacade.order(savedMember.getMemberId(),
                                                              CreateOrderWithCouponRequest.create(List.of(createOrderRequest), savedCoupon.getId()))
                                                       .getOrderInfos();

        BigDecimal totalPrice = savedProduct.getPrice().multiply(BigDecimal.valueOf(createOrderRequest.quantity()));

        assertAll(
                () -> assertThat(orderProductInfos.get(0).productName()).isEqualTo(savedProduct.getName()),
                () -> assertThat(orderProductInfos.get(0).totalQuantity()).isEqualTo(createOrderRequest.quantity()),
                () -> assertThat(orderProductInfos.get(0).totalPrice().compareTo(totalPrice)).isZero()
        );
    }
}
