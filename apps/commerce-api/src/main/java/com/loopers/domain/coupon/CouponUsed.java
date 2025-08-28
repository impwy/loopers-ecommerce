package com.loopers.domain.coupon;

import com.loopers.domain.member.MemberId;

public record CouponUsed(Long couponId, MemberId memberId) {
}
