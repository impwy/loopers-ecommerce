package com.loopers.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CouponTest {
    @DisplayName("쿠폰 code null check")
    @Test
    void throw_nullPointException_when_code_is_null() {
        assertThatThrownBy(() -> Coupon.create(new CreateCouponSpec(null, 1L, DiscountPolicy.AMOUNT, CouponType.MEMBER)))
            .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("쿠폰 DiscountPolicy null check")
    @Test
    void throw_nullPointException_when_discountPolicy_is_null() {
        assertThatThrownBy(() -> Coupon.create(new CreateCouponSpec("coupon", 1L, null, CouponType.MEMBER)))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("쿠폰 couponType null check")
    @Test
    void throw_nullPointException_when_couponType_is_null() {
        assertThatThrownBy(() -> Coupon.create(new CreateCouponSpec("coupon", 1L, DiscountPolicy.AMOUNT, null)))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("쿠폰 생성 테스트")
    @Test
    void createCoupon() {
        Coupon coupon = Coupon.create(CouponFixture.createCouponSpec());

        assertThat(coupon.getCode()).isEqualTo("coupon");
        assertThat(coupon.getDiscountPolicy()).isEqualTo(DiscountPolicy.AMOUNT);
        assertThat(coupon.getCouponType()).isEqualTo(CouponType.MEMBER);
    }
}
