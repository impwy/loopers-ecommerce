package com.loopers.infrastructure.coupon.membercoupon;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.membercoupon.MemberCoupon;
import com.loopers.domain.member.Member;

public interface MemberCouponJpaRepository extends JpaRepository<MemberCoupon, Long> {
    boolean existsByMemberAndCoupon(Member member, Coupon coupon);
}
