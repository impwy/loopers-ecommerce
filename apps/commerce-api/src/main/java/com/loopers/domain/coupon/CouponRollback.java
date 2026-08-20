package com.loopers.domain.coupon;

import com.loopers.domain.member.UserId;

public record CouponRollback(UserId userId, String orderId) {
}
