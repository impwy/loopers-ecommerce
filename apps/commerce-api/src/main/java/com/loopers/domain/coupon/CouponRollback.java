package com.loopers.domain.coupon;

import com.loopers.domain.member.MemberId;

public record CouponRollback(MemberId memberId, String orderId) {
}
