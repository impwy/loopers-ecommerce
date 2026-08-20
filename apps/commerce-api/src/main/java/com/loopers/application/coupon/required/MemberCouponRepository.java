package com.loopers.application.coupon.required;

import org.springframework.data.repository.Repository;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.couponusage.CouponUsage;
import com.loopers.domain.member.Member;

public interface MemberCouponRepository extends Repository<CouponUsage, Long> {

    boolean existsByMemberAndCoupon(Member member, Coupon coupon);
}
