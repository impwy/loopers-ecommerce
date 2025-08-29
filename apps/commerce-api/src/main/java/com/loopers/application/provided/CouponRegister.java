package com.loopers.application.provided;

import java.math.BigDecimal;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CreateCouponSpec;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;

public interface CouponRegister {
    Coupon create(CreateCouponSpec createCouponSpec);

    Coupon useMemberCoupon(Long couponId, Member member);

    BigDecimal discountPrice(Long couponId, Member member, BigDecimal totalAmount);

    void rollback(MemberId memberId, Long couponId);
}
