package com.loopers.application.coupon.required;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponFixture;
import com.loopers.domain.couponusage.CouponUsage;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.UserId;
import com.loopers.support.BaseRepositoryTest;
import com.loopers.support.stereotype.ApplicationJpaServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationJpaServiceTest
@RequiredArgsConstructor
class CouponRepositoryTest extends BaseRepositoryTest {
    final CouponRepository couponRepository;

    @Test
    void saveAndFindById() {
        Coupon coupon = couponRepository.save(CouponFixture.createCoupon());
        flushAndClear();

        Coupon found = couponRepository.findById(coupon.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(coupon.getId());
        assertThat(found.getCode()).isEqualTo(coupon.getCode());
    }

    @Test
    void findWithPessimisticLock() {
        Coupon coupon = couponRepository.save(CouponFixture.createCoupon());
        flushAndClear();

        Coupon found = couponRepository.findWithPessimisticLock(coupon.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(coupon.getId());
    }

    @Test
    void findByMemberIdAndCouponId() {
        Member member = prepareMember();
        Coupon coupon = CouponFixture.createCoupon();
        CouponUsage couponUsage = CouponUsage.create(member, coupon);
        coupon.addMemberCoupon(couponUsage);
        coupon = couponRepository.save(coupon);
        flushAndClear();

        CouponUsage found = couponRepository.findByUserIdAndCouponId(
                new UserId(member.getUserId().value()), coupon.getId()
        ).orElseThrow();

        assertThat(found.getId()).isNotNull();
        assertThat(found.getCoupon().getId()).isEqualTo(coupon.getId());
    }
}
