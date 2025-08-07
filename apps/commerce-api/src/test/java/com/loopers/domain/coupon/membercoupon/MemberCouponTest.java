package com.loopers.domain.coupon.membercoupon;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponFixture;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;

class MemberCouponTest {
    @Test
    void createMemberCoupon() {
        Member member = MemberFixture.createMember();
        Coupon coupon = CouponFixture.createCoupon();
        MemberCoupon memberCoupon = MemberCoupon.create(member, coupon);

        assertThat(memberCoupon.getMember().getMemberId().memberId()).isEqualTo(member.getMemberId().memberId());
        assertThat(memberCoupon.getCoupon().getCode()).isEqualTo(coupon.getCode());
    }
}
