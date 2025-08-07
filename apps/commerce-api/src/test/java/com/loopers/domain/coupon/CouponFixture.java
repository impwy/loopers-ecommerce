package com.loopers.domain.coupon;

public class CouponFixture {
    public static Coupon createCoupon() {
        return Coupon.create(createCouponSpec());
    }

    public static CreateCouponSpec createCouponSpec() {
        return new CreateCouponSpec("coupon", DiscountPolicy.FIXED, CouponType.MEMBER);
    }
}
