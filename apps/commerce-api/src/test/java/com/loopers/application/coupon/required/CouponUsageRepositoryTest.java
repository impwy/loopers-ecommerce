package com.loopers.application.coupon.required;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.couponusage.CouponUsage;
import com.loopers.domain.member.Member;
import com.loopers.support.BaseRepositoryTest;
import com.loopers.support.stereotype.ApplicationJpaServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationJpaServiceTest
@RequiredArgsConstructor
class CouponUsageRepositoryTest extends BaseRepositoryTest {
    final MemberCouponRepository memberCouponRepository;

    @Test
    void existsByMemberAndCoupon() {
        Member member = prepareMember();
        Coupon coupon = prepareCoupon();
        CouponUsage couponUsage = CouponUsage.create(member, coupon);
        coupon.addMemberCoupon(couponUsage);
        saveCoupon(coupon);
        flushAndClear();

        boolean exists = memberCouponRepository.existsByMemberAndCoupon(member, coupon);

        assertThat(exists).isTrue();
    }
}
