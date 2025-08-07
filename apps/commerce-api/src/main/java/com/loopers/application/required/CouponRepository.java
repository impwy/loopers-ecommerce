package com.loopers.application.required;

import java.util.Optional;

import com.loopers.domain.coupon.Coupon;

public interface CouponRepository {
    Coupon create(Coupon coupon);

    Optional<Coupon> find(Long couponId);

    Optional<Coupon> findWithPessimisticLock(Long couponId);
}
