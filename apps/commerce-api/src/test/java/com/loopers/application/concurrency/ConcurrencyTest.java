package com.loopers.application.concurrency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.loopers.application.inventory.DecreaseInventoryRequest;
import com.loopers.application.coupon.provided.CouponRegister;
import com.loopers.application.inventory.provided.InventoryRegister;
import com.loopers.application.member.provided.MemberFinder;
import com.loopers.application.member.provided.MemberRegister;
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
import com.loopers.domain.inventory.Inventory;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductFixture;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest
class ConcurrencyTest {

    private static final long CONCURRENCY_TIMEOUT_SECONDS = 60L;

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

    private void runConcurrently(int threadCount, Runnable operation) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startBarrier = new CountDownLatch(threadCount);
        List<Future<?>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < threadCount; i++) {
                futures.add(executorService.submit(() -> {
                    startBarrier.countDown();
                    if (!startBarrier.await(CONCURRENCY_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("동시성 테스트 시작 신호를 기다리다 시간 초과되었습니다.");
                    }
                    operation.run();
                    return null;
                }));
            }

            for (Future<?> future : futures) {
                future.get(CONCURRENCY_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            }
        } finally {
            executorService.shutdownNow();
            executorService.awaitTermination(CONCURRENCY_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }
    }

    @DisplayName("동시 실행 도우미 테스트")
    @Nested
    class ConcurrentRunnerTest {

        @Test
        void executes_operation_for_every_thread() throws Exception {
            int threadCount = 10;
            AtomicInteger executionCount = new AtomicInteger();

            runConcurrently(threadCount, executionCount::incrementAndGet);

            assertThat(executionCount).hasValue(threadCount);
        }

        @Test
        void propagates_worker_exception_to_test_thread() {
            AtomicBoolean firstExecution = new AtomicBoolean(true);

            assertThatThrownBy(() -> runConcurrently(5, () -> {
                if (firstExecution.compareAndSet(true, false)) {
                    throw new IllegalStateException("worker failure");
                }
            }))
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseInstanceOf(IllegalStateException.class)
                    .hasRootCauseMessage("worker failure");
        }
    }

    @DisplayName("재고 차감 동시성 테스트")
    @Nested
    class InventoryConcurrencyTest {
        @Test
        void inventory_concurrency_test() throws Exception {
            int threadCount = 100;

            Brand brand = brandRepository.create(BrandFixture.createBrand());
            Product product = productRepository.save(ProductFixture.createProduct(brand));
            Inventory inventory = inventoryRepository.save(Inventory.of(CreateInventorySpec.of(product.getId(), 1000L)));

            DecreaseInventoryRequest createOrderRequest = new DecreaseInventoryRequest(product.getId(), 10L);

            runConcurrently(threadCount,
                            () -> inventoryRegister.decreaseProducts(List.of(createOrderRequest)));

            Inventory updatedInventory = inventoryRepository.find(inventory.getId()).orElseThrow();
            assertThat(updatedInventory.getQuantity()).isEqualTo(0);
        }

        @Test
        void inventory_does_not_go_below_zero_when_requests_exceed_stock() throws Exception {
            int threadCount = 10;

            Brand brand = brandRepository.create(BrandFixture.createBrand());
            Product product = productRepository.save(ProductFixture.createProduct(brand));
            Inventory inventory = inventoryRepository.save(Inventory.of(CreateInventorySpec.of(product.getId(), 50L)));
            DecreaseInventoryRequest request = new DecreaseInventoryRequest(product.getId(), 10L);
            AtomicInteger successCount = new AtomicInteger();
            AtomicInteger failureCount = new AtomicInteger();

            runConcurrently(threadCount, () -> {
                try {
                    inventoryRegister.decreaseProducts(List.of(request));
                    successCount.incrementAndGet();
                } catch (CoreException e) {
                    assertThat(e.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
                    failureCount.incrementAndGet();
                }
            });

            Inventory updatedInventory = inventoryRepository.find(inventory.getId()).orElseThrow();
            assertThat(updatedInventory.getQuantity()).isZero();
            assertThat(successCount).hasValue(5);
            assertThat(failureCount).hasValue(threadCount - 5);
        }
    }

    @DisplayName("포인트 동시성 테스트")
    @Nested
    class PointConcurrencyTest {
        @Test
        void point_concurrency_test() throws Exception {
            int threadCount = 10;

            Member member = MemberFixture.createMember();
            member.charge(BigDecimal.valueOf(10000000));
            Member savedMember = memberRepository.save(member);

            runConcurrently(threadCount,
                            () -> memberRegister.usePoint(member.getMemberId(), BigDecimal.valueOf(1000000)));

            Member updatedMember = memberFinder.findByMemberId(savedMember.getMemberId());
            assertThat(updatedMember.getPoint().getAmount().compareTo(BigDecimal.valueOf(0))).isZero();
        }

        @Test
        void point_does_not_go_below_zero_when_requests_exceed_balance() throws Exception {
            int threadCount = 10;

            Member member = MemberFixture.createMember();
            member.charge(BigDecimal.valueOf(5_000_000));
            Member savedMember = memberRepository.save(member);
            AtomicInteger successCount = new AtomicInteger();
            AtomicInteger failureCount = new AtomicInteger();

            runConcurrently(threadCount, () -> {
                try {
                    memberRegister.usePoint(member.getMemberId(), BigDecimal.valueOf(1_000_000));
                    successCount.incrementAndGet();
                } catch (CoreException e) {
                    assertThat(e.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
                    failureCount.incrementAndGet();
                }
            });

            Member updatedMember = memberFinder.findByMemberId(savedMember.getMemberId());
            assertThat(updatedMember.getPoint().getAmount()).isZero();
            assertThat(successCount).hasValue(5);
            assertThat(failureCount).hasValue(threadCount - 5);
        }
    }

    @DisplayName("쿠폰 동시성 테스트")
    @Nested
    class CouponConcurrencyTest {
        @Test
        void coupon_concurrency_test() throws Exception {
            int threadCount = 10;

            Member member = MemberFixture.createMember();
            Member savedMember = memberRepository.save(member);

            Coupon coupon = couponRepository.create(Coupon.create(CreateCouponSpec.create("testCoupon",
                                                                                          100L,
                                                                                          DiscountPolicy.AMOUNT,
                                                                                          CouponType.MEMBER)));

            AtomicInteger successCount = new AtomicInteger();
            AtomicInteger conflictCount = new AtomicInteger();

            runConcurrently(threadCount, () -> {
                try {
                    couponRegister.useMemberCoupon(coupon.getId(), savedMember);
                    successCount.incrementAndGet();
                } catch (CoreException e) {
                    assertThat(e.getErrorType()).isEqualTo(ErrorType.CONFLICT);
                    conflictCount.incrementAndGet();
                }
            });

            Coupon updatedCoupon = couponRepository.find(coupon.getId()).orElseThrow();
            assertThat(updatedCoupon.getQuantity()).isEqualTo(99L);
            assertThat(successCount).hasValue(1);
            assertThat(conflictCount).hasValue(threadCount - 1);
        }
    }
}
