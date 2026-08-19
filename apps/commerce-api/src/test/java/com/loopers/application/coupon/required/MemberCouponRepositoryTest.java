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
import com.loopers.support.stereotype.ApplicationJpaServiceTest;

import lombok.RequiredArgsConstructor;

@ApplicationJpaServiceTest
@RequiredArgsConstructor
class MemberCouponRepositoryTest {
    final MemberCouponRepository memberCouponRepository;
    final MemberRepository memberRepository;
    final EntityManager entityManager;

    @Test
    void existsByMemberAndCoupon() {
        Member member = memberRepository.save(MemberFixture.createMember());
        Coupon coupon = CouponFixture.createCoupon();
        MemberCoupon memberCoupon = MemberCoupon.create(member, coupon);
        coupon.addMemberCoupon(memberCoupon);
        entityManager.persist(coupon);
        entityManager.flush();
        entityManager.clear();

        boolean exists = memberCouponRepository.existsByMemberAndCoupon(member, coupon);

        assertThat(exists).isTrue();
    }
}
