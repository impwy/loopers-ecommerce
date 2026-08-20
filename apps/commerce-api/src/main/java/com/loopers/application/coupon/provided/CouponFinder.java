package com.loopers.application.coupon.provided;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.couponusage.CouponUsage;
import com.loopers.domain.member.UserId;

public interface CouponFinder {
    Coupon find(Long couponId);

    Coupon findWithPessimisticLock(Long couponId);

    CouponUsage findMemberCoupon(UserId userId, Long couponId);
}
