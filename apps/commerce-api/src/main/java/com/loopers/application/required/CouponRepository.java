package com.loopers.application.required;

import java.util.Optional;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.membercoupon.MemberCoupon;
import com.loopers.domain.member.MemberId;

public interface CouponRepository {
    Coupon create(Coupon coupon);

    Optional<Coupon> find(Long couponId);

    Optional<Coupon> findWithPessimisticLock(Long couponId);

    Optional<MemberCoupon> findByMemberIdAndCouponId(MemberId memberId, Long couponId);
}
