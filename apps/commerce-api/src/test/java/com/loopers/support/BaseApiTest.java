package com.loopers.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.transaction.PlatformTransactionManager;

import com.loopers.adapter.webapi.ApiResponse;
import com.loopers.adapter.webapi.member.dto.MemberV1Dto;
import com.loopers.application.brand.required.BrandRepository;
import com.loopers.application.coupon.required.CouponRepository;
import com.loopers.application.inventory.required.InventoryRepository;
import com.loopers.application.member.MemberRegisterRequest;
import com.loopers.application.member.required.MemberRepository;
import com.loopers.application.product.required.ProductRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandFixture;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponFixture;
import com.loopers.domain.coupon.CreateCouponSpec;
import com.loopers.domain.couponusage.CouponUsage;
import com.loopers.domain.inventory.Inventory;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.member.UserId;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductFixture;
import com.loopers.utils.DatabaseCleanUp;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
public class BaseApiTest {
    protected static final String MEMBER_ENDPOINT = "/api/v1/members";

    @Autowired
    protected RestTestClient restTestClient;
    @Autowired
    protected MemberRepository memberRepository;
    @Autowired
    protected BrandRepository brandRepository;
    @Autowired
    protected ProductRepository productRepository;
    @Autowired
    protected InventoryRepository inventoryRepository;
    @Autowired
    protected CouponRepository couponRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void cleanUpAfterEach() {
        if (TestTransaction.isActive()) {
            TestTransaction.flagForRollback();
            TestTransaction.end();
        }
        databaseCleanUp.truncateAllTables();
    }

    /**
     * Creates a member through the public HTTP API and reloads it in a new transaction.
     */
    protected Member prepareMember() {
        MemberRegisterRequest request = MemberFixture.createMemberRegisterRequest();
        ParameterizedTypeReference<ApiResponse<MemberV1Dto.MemberRegisterResponse>> responseType =
                new ParameterizedTypeReference<>() {};

        EntityExchangeResult<ApiResponse<MemberV1Dto.MemberRegisterResponse>> result =
                restTestClient.post()
                              .uri(MEMBER_ENDPOINT)
                              .contentType(MediaType.APPLICATION_JSON)
                              .body(request)
                              .exchange()
                              .expectStatus().isOk()
                              .expectBody(responseType)
                              .returnResult();

        ApiResponse<MemberV1Dto.MemberRegisterResponse> response = result.getResponseBody();
        assertThat(response).as("회원 가입 응답 본문").isNotNull();
        assertThat(response.data()).as("회원 가입 응답 데이터").isNotNull();

        String memberId = response.data().userId();
        return memberRepository.findByUserId(new UserId(memberId)).orElseThrow();
    }

    protected Brand prepareBrand() {
        return prepareBrand(BrandFixture.createBrand());
    }

    protected Brand prepareBrand(String name, String description) {
        return prepareBrand(name, description, LocalDate.of(2000, 1, 1));
    }

    protected Brand prepareBrand(String name, String description, LocalDate since) {
        return prepareBrand(Brand.create(name, description, since));
    }

    private Brand prepareBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    protected Product prepareProduct(Brand brand) {
        Long brandId = brand.getId();
        Brand managedBrand = brandRepository.findById(brandId).orElseThrow();
        return productRepository.save(ProductFixture.createProduct(managedBrand));
    }

    protected Product prepareProduct(Brand brand, String name, String description,
                                     BigDecimal price, ZonedDateTime latestAt) {
        Long brandId = brand.getId();
        Brand managedBrand = brandRepository.findById(brandId).orElseThrow();
        return productRepository.save(Product.create(name, description, price, managedBrand, latestAt));
    }

    protected Inventory prepareInventory(Product product, Long quantity) {
        return prepareInventory(product.getId(), quantity);
    }

    protected Inventory prepareInventory(Long productId, Long quantity) {
        return inventoryRepository.save(Inventory.create(productId, quantity));
    }

    protected Coupon prepareCoupon() {
        return prepareCoupon(CouponFixture.createCouponSpec());
    }

    protected Coupon prepareCoupon(CreateCouponSpec spec) {
        return couponRepository.save(Coupon.create(spec));
    }

    protected Coupon prepareCoupon(CreateCouponSpec spec, Member member) {
        Long memberId = member.getId();
        Member managedMember = memberRepository.findById(memberId).orElseThrow();
        Coupon coupon = couponRepository.save(Coupon.create(spec));
        coupon.addMemberCoupon(CouponUsage.create(managedMember, coupon));
        return couponRepository.save(coupon);
    }
}
