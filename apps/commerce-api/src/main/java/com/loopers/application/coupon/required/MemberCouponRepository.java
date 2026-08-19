package com.loopers.application.coupon.required;

import org.springframework.data.repository.Repository;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.membercoupon.MemberCoupon;
import com.loopers.domain.member.Member;

public interface MemberCouponRepository extends Repository<MemberCoupon, Long> {

    boolean existsByMemberAndCoupon(Member member, Coupon coupon);
}
