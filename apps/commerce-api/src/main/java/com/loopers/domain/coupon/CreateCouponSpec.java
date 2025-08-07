package com.loopers.domain.coupon;

public record CreateCouponSpec(String code, DiscountPolicy discountPolicy, CouponType couponType) {
    public static CreateCouponSpec create(String code, DiscountPolicy discountPolicy, CouponType couponType) {
        return new CreateCouponSpec(code, discountPolicy, couponType);
    }
}
