package com.loopers.support;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.loopers.application.brand.required.BrandRepository;
import com.loopers.application.coupon.required.CouponRepository;
import com.loopers.application.like.required.ProductLikeRepository;
import com.loopers.application.member.required.MemberRepository;
import com.loopers.application.order.required.OrderRepository;
import com.loopers.application.product.required.ProductRepository;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandFixture;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponFixture;
import com.loopers.domain.coupon.CreateCouponSpec;
import com.loopers.domain.like.ProductLike;
import com.loopers.domain.like.ProductLikeFixture;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderFixture;
import com.loopers.domain.order.orderitem.CreateOrderItemSpec;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductFixture;
import com.loopers.support.stereotype.ApplicationJpaServiceTest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationJpaServiceTest
public class BaseRepositoryTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductLikeRepository productLikeRepository;

    protected void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    protected Member prepareMember() {
        return memberRepository.save(MemberFixture.createMember());
    }

    protected Member prepareMember(String key) {
        return memberRepository.save(MemberFixture.createMember(key));
    }

    protected Brand prepareBrand() {
        return brandRepository.save(BrandFixture.createBrand());
    }

    protected Brand prepareBrand(String name) {
        return brandRepository.save(BrandFixture.createBrand(name));
    }

    protected Product prepareProduct(Brand brand) {
        return productRepository.save(ProductFixture.createProduct(brand));
    }

    protected Coupon prepareCoupon() {
        return couponRepository.save(CouponFixture.createCoupon());
    }

    protected Coupon prepareCoupon(CreateCouponSpec spec) {
        return couponRepository.save(Coupon.create(spec));
    }

    protected Coupon saveCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    protected Order prepareOrder() {
        return prepareOrder(1L);
    }

    protected Order prepareOrder(Long memberId) {
        return orderRepository.save(OrderFixture.createOrder(memberId));
    }

    protected Order prepareOrderWithItems(Long memberId, List<CreateOrderItemSpec> itemSpecs) {
        Order order = OrderFixture.createOrder(memberId).createOrderItems(itemSpecs);
        return orderRepository.save(order);
    }

    protected ProductLike prepareProductLike(Member member, Product product) {
        return productLikeRepository.save(ProductLikeFixture.createProductLike(member, product));
    }
}
