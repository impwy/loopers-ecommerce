package com.loopers.support;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.loopers.application.brand.BrandCreateRequest;
import com.loopers.application.brand.provided.BrandRegister;
import com.loopers.application.coupon.provided.CouponRegister;
import com.loopers.application.inventory.provided.InventoryRegister;
import com.loopers.application.like.provided.ProductLikeRegister;
import com.loopers.application.member.MemberRegisterRequest;
import com.loopers.application.member.provided.MemberRegister;
import com.loopers.application.member.provided.MemberFinder;
import com.loopers.application.order.provided.OrderRegister;
import com.loopers.application.product.provided.ProductRegister;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandFixture;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponFixture;
import com.loopers.domain.coupon.CreateCouponSpec;
import com.loopers.domain.inventory.Inventory;
import com.loopers.domain.inventory.InventoryFixture;
import com.loopers.domain.like.ProductLike;
import com.loopers.domain.like.ProductLikeFixture;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderFixture;
import com.loopers.domain.order.orderitem.CreateOrderItemSpec;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductFixture;
import com.loopers.support.stereotype.ApplicationServiceTest;

@ApplicationServiceTest
public class BaseApplicationServiceTest {
    @Autowired
    private MemberRegister memberRegister;
    @Autowired
    private MemberFinder memberFinder;
    @Autowired
    private BrandRegister brandRegister;
    @Autowired
    private ProductRegister productRegister;
    @Autowired
    private InventoryRegister inventoryRegister;
    @Autowired
    private CouponRegister couponRegister;
    @Autowired
    private OrderRegister orderRegister;
    @Autowired
    private ProductLikeRegister productLikeRegister;

    protected Member prepareMember() {
        return prepareMember(MemberFixture.createMemberRegisterRequest());
    }

    protected Member prepareMember(String key) {
        return prepareMember(MemberFixture.createMemberRegisterRequest(key));
    }

    protected Member prepareMember(MemberRegisterRequest request) {
        Member member = memberRegister.register(request);
        return memberFinder.findByUserId(member.getUserId());
    }

    protected Brand prepareBrand() {
        return brandRegister.create(BrandFixture.createBrandCreateRequest());
    }

    protected Brand prepareBrand(String name) {
        return prepareBrand(name, "브랜드 설명", LocalDate.of(2000, 1, 1));
    }

    protected Brand prepareBrand(BrandCreateRequest request) {
        return brandRegister.create(request);
    }

    protected Brand prepareBrand(String name, String description, LocalDate since) {
        return prepareBrand(BrandFixture.createBrandCreateRequest(name, description, since));
    }

    protected Product prepareProduct() {
        return prepareProduct(prepareBrand());
    }

    protected Product prepareProduct(Brand brand) {
        return productRegister.register(ProductFixture.createProductSpec(brand));
    }

    protected Product prepareProduct(Brand brand, String name, String description,
                                     BigDecimal price, ZonedDateTime latestAt) {
        return productRegister.register(ProductFixture.createProductSpec(name, description, price, brand, latestAt));
    }

    protected Inventory prepareInventory(Product product, Long quantity) {
        return prepareInventory(product.getId(), quantity);
    }

    protected Inventory prepareInventory(Long productId, Long quantity) {
        return inventoryRegister.register(InventoryFixture.createInventorySpec(productId, quantity));
    }

    protected Coupon prepareCoupon() {
        return prepareCoupon(CouponFixture.createCouponSpec());
    }

    protected Coupon prepareCoupon(CreateCouponSpec spec) {
        return couponRegister.create(spec);
    }

    protected Order prepareOrder() {
        return prepareOrder(1L);
    }

    protected Order prepareOrder(Long memberId) {
        return prepareOrder(memberId, List.of());
    }

    protected Order prepareOrder(Long memberId, List<CreateOrderItemSpec> itemSpecs) {
        return orderRegister.createOrder(OrderFixture.createOrderSpec(memberId), itemSpecs);
    }

    protected ProductLike prepareProductLike(Member member, Product product) {
        return productLikeRegister.create(ProductLikeFixture.createProductLike(member, product));
    }
}
