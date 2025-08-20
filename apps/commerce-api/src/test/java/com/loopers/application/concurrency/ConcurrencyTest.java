package com.loopers.application.concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.provided.CouponRegister;
import com.loopers.application.provided.InventoryRegister;
import com.loopers.application.provided.MemberFinder;
import com.loopers.application.provided.MemberRegister;
import com.loopers.application.required.BrandRepository;
import com.loopers.application.required.CouponRepository;
import com.loopers.application.required.InventoryRepository;
import com.loopers.application.required.MemberRepository;
import com.loopers.application.required.ProductRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandFixture;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponType;
import com.loopers.domain.coupon.CreateCouponSpec;
import com.loopers.domain.coupon.DiscountPolicy;
import com.loopers.domain.inventory.CreateInventorySpec;
import com.loopers.domain.inventory.DecreaseInventoryRequest;
import com.loopers.domain.inventory.Inventory;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductFixture;
import com.loopers.interfaces.api.order.dto.OrderV1Dto.Request.CreateOrderRequest;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest
class ConcurrencyTest {

    @MockitoSpyBean
    private ProductRepository productRepository;

    @MockitoSpyBean
    private BrandRepository brandRepository;

    @MockitoSpyBean
    private InventoryRepository inventoryRepository;

    @MockitoSpyBean
    private MemberRepository memberRepository;

    @MockitoSpyBean
    private CouponRepository couponRepository;

    @Autowired
    private InventoryRegister inventoryRegister;

    @Autowired
    private MemberRegister memberRegister;

    @Autowired
    private CouponRegister couponRegister;

    @Autowired
    private MemberFinder memberFinder;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("재고 차감 동시성 테스트")
    @Nested
    class InventoryConcurrencyTest {
        @Test
        void inventory_concurrency_test() throws InterruptedException {
            int threadCount = 100;
            // thread 수가 많을 수록 테스트 속도 증가.
            // threadCount가 증가할 수록 테스트 시간 증가
            ExecutorService executorService = Executors.newFixedThreadPool(100);

            // count를 늘려가며 지정한 count가 되면 thread 전부 시작.
            CountDownLatch readyLatch = new CountDownLatch(100);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(100);

            Brand brand = brandRepository.create(BrandFixture.createBrand());
            Product product = productRepository.save(ProductFixture.createProduct(brand));
            Inventory inventory = inventoryRepository.save(Inventory.of(CreateInventorySpec.of(product.getId(), 1000L)));

            DecreaseInventoryRequest createOrderRequest = new DecreaseInventoryRequest(product.getId(), 10L);

            for (int i = 0; i < threadCount; i++) {
                executorService.execute(() -> {
                    try {
                        readyLatch.countDown();
                        startLatch.await();
                        inventoryRegister.decreaseProducts(List.of(createOrderRequest));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            readyLatch.await();
            startLatch.countDown();
            doneLatch.await();

            Inventory updatedInventory = inventoryRepository.find(inventory.getId()).orElseThrow();
            assertThat(updatedInventory.getQuantity()).isEqualTo(0);

            executorService.shutdown();
        }
    }

    @DisplayName("포인트 동시성 테스트")
    @Nested
    class PointConcurrencyTest {
        @Test
        void point_concurrency_test() throws InterruptedException {
            int threadCount = 10;
            ExecutorService executorService = Executors.newFixedThreadPool(100);

            CountDownLatch readyLatch = new CountDownLatch(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threadCount);

            Member member = MemberFixture.createMember();
            member.charge(BigDecimal.valueOf(10000000));
            Member savedMember = memberRepository.save(member);

            for (int i = 0; i < threadCount; i++) {
                executorService.execute(() -> {
                    try {
                        readyLatch.countDown();
                        startLatch.await();
                        memberRegister.usePoint(member.getMemberId(), BigDecimal.valueOf(1000000));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            readyLatch.await();
            startLatch.countDown();
            doneLatch.await();

            Member updatedMember = memberFinder.findByMemberId(savedMember.getMemberId());
            assertThat(updatedMember.getPoint().getAmount().compareTo(BigDecimal.valueOf(0))).isZero();

            executorService.shutdown();
        }
    }

    @DisplayName("쿠폰 동시성 테스트")
    @Nested
    class CouponConcurrencyTest {
        @Test
        void coupon_concurrency_test() throws InterruptedException {
            int threadCount = 10;
            ExecutorService executorService = Executors.newFixedThreadPool(100);

            CountDownLatch readyLatch = new CountDownLatch(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threadCount);

            Member member = MemberFixture.createMember();
            Member savedMember = memberRepository.save(member);

            Coupon coupon = couponRepository.create(Coupon.create(CreateCouponSpec.create("testCoupon",
                                                                                          100L,
                                                                                          DiscountPolicy.AMOUNT,
                                                                                          CouponType.MEMBER)));

            for (int i = 0; i < threadCount; i++) {
                executorService.execute(() -> {
                    try {
                        readyLatch.countDown();
                        startLatch.await();
                        couponRegister.useMemberCoupon(coupon.getId(), savedMember);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            readyLatch.await();
            startLatch.countDown();
            doneLatch.await();

            Coupon updatedCoupon = couponRepository.find(coupon.getId()).orElseThrow();
            assertThat(updatedCoupon.getQuantity()).isEqualTo(99L);

            executorService.shutdown();
        }
    }
}
