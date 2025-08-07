package com.loopers.domain.coupon;

import com.loopers.interfaces.api.coupon.dto.CouponV1Dto.Request.CreateCouponRequest;

public class CouponFixture {
    public static Coupon createCoupon() {
        return Coupon.create(createCouponSpec());
    }

    public static CreateCouponSpec createCouponSpec() {
        return new CreateCouponSpec("coupon", 1L, DiscountPolicy.AMOUNT, CouponType.MEMBER);
    }

    public static CreateCouponRequest createCouponRequest() {
        return new CreateCouponRequest("coupon", 1L, DiscountPolicy.AMOUNT, CouponType.MEMBER);
    }
}
