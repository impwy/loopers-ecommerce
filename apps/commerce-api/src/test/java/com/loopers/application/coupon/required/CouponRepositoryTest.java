package com.loopers.application.coupon.required;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import com.loopers.application.member.required.MemberRepository;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponFixture;
import com.loopers.domain.coupon.membercoupon.MemberCoupon;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberFixture;
import com.loopers.domain.member.MemberId;
import com.loopers.support.stereotype.ApplicationJpaServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationJpaServiceTest
@RequiredArgsConstructor
class CouponRepositoryTest {
    final CouponRepository couponRepository;
    final MemberRepository memberRepository;
    final EntityManager entityManager;

    @Test
    void saveAndFindById() {
        Coupon coupon = couponRepository.save(CouponFixture.createCoupon());
        entityManager.flush();
        entityManager.clear();

        Coupon found = couponRepository.findById(coupon.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(coupon.getId());
        assertThat(found.getCode()).isEqualTo(coupon.getCode());
    }

    @Test
    void findWithPessimisticLock() {
        Coupon coupon = couponRepository.save(CouponFixture.createCoupon());
        entityManager.flush();
        entityManager.clear();

        Coupon found = couponRepository.findWithPessimisticLock(coupon.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(coupon.getId());
    }

    @Test
    void findByMemberIdAndCouponId() {
        Member member = memberRepository.save(MemberFixture.createMember());
        Coupon coupon = CouponFixture.createCoupon();
        MemberCoupon memberCoupon = MemberCoupon.create(member, coupon);
        coupon.addMemberCoupon(memberCoupon);
        coupon = couponRepository.save(coupon);
        entityManager.flush();
        entityManager.clear();

        MemberCoupon found = couponRepository.findByMemberIdAndCouponId(
                new MemberId(member.getMemberId().memberId()), coupon.getId()
        ).orElseThrow();

        assertThat(found.getId()).isNotNull();
        assertThat(found.getCoupon().getId()).isEqualTo(coupon.getId());
    }
}
