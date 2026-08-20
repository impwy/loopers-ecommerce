package com.loopers.domain.coupon.couponusage;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponFixture;
import com.loopers.domain.couponusage.CouponUsage;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;

class CouponUsageTest {
    @Test
    void createMemberCoupon() {
        Member member = MemberFixture.createMember();
        Coupon coupon = CouponFixture.createCoupon();
        CouponUsage couponUsage = CouponUsage.create(member, coupon);

        assertThat(couponUsage.getMember().getUserId().userId()).isEqualTo(member.getUserId().userId());
        assertThat(couponUsage.getCoupon().getCode()).isEqualTo(coupon.getCode());
    }
}
