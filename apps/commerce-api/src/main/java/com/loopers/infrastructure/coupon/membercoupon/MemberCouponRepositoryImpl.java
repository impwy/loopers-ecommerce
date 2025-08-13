package com.loopers.infrastructure.coupon.membercoupon;

import org.springframework.stereotype.Component;

import com.loopers.application.required.MemberCouponRepository;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.member.Member;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberCouponRepositoryImpl implements MemberCouponRepository {
    private final MemberCouponJpaRepository memberCouponJpaRepository;

    @Override
    public boolean hasMemberCoupon(Member member, Coupon coupon) {
        return memberCouponJpaRepository.existsByMemberAndCoupon(member, coupon);
    }
}
