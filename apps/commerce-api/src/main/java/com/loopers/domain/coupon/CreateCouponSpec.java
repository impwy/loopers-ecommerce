package com.loopers.domain.coupon;

public record CreateCouponSpec(String code, Long quantity, DiscountPolicy discountPolicy, CouponType couponType) {
    public static CreateCouponSpec create(String code, Long quantity, DiscountPolicy discountPolicy, CouponType couponType) {
        return new CreateCouponSpec(code, quantity, discountPolicy, couponType);
    }
}
