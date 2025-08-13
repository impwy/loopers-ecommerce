package com.loopers.application.provided;

import com.loopers.domain.coupon.Coupon;

public interface CouponFinder {
    Coupon find(Long couponId);

    Coupon findWithPessimisticLock(Long couponId);
}
