package com.loopers.application.coupon;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.CouponFinder;
import com.loopers.application.required.CouponRepository;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.membercoupon.MemberCoupon;
import com.loopers.domain.member.MemberId;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponQueryService implements CouponFinder {
    private final CouponRepository couponRepository;

    @Override
    public Coupon find(Long couponId) {
        return couponRepository.find(couponId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 번호입니다." + couponId));
    }

    @Override
    public Coupon findWithPessimisticLock(Long couponId) {
        return couponRepository.findWithPessimisticLock(couponId)
                               .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 번호입니다." + couponId));
    }

    @Override
    public MemberCoupon findMemberCoupon(MemberId memberId, Long couponId) {
        return couponRepository.findByMemberIdAndCouponId(memberId, couponId)
                               .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 입니다." + couponId));
    }
}
