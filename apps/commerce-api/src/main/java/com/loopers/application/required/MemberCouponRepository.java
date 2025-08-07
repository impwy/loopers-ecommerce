package com.loopers.application.required;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.member.Member;

public interface MemberCouponRepository {
    boolean hasMemberCoupon(Member member, Coupon coupon);
}
