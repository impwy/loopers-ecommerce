package com.loopers.application.provided;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.membercoupon.MemberCoupon;
import com.loopers.domain.member.MemberId;

public interface CouponFinder {
    Coupon find(Long couponId);

    Coupon findWithPessimisticLock(Long couponId);

    MemberCoupon findMemberCoupon(MemberId memberId, Long couponId);
}
