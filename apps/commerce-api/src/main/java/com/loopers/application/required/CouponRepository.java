package com.loopers.application.required;

import com.loopers.domain.coupon.Coupon;

public interface CouponRepository {
    Coupon create(Coupon coupon);
}
