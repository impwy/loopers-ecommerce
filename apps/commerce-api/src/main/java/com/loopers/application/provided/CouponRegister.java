package com.loopers.application.provided;

import com.loopers.domain.coupon.Coupon;
import com.loopers.interfaces.api.coupon.dto.CouponV1Dto.Request.CreateCouponRequest;

public interface CouponRegister {
    Coupon create(CreateCouponRequest couponRequest);
}
