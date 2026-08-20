package com.loopers.domain.coupon;

import com.loopers.domain.member.UserId;

public record CouponUsed(Long couponId, UserId userId) {
}
