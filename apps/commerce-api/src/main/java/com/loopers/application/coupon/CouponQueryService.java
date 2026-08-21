package com.loopers.application.coupon;

import com.loopers.application.coupon.provided.CouponFinder;
import com.loopers.application.coupon.required.CouponRepository;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.couponusage.CouponUsage;
import com.loopers.domain.member.UserId;
import com.loopers.shared.stereotype.ApplicationService;

import lombok.RequiredArgsConstructor;

@ApplicationService
@RequiredArgsConstructor
public class CouponQueryService implements CouponFinder {
    private final CouponRepository couponRepository;

    @Override
    public Coupon find(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 번호입니다." + couponId));
    }

    @Override
    public Coupon findWithPessimisticLock(Long couponId) {
        return couponRepository.findWithPessimisticLock(couponId)
                               .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 번호입니다." + couponId));
    }

    @Override
    public CouponUsage findMemberCoupon(UserId userId, Long couponId) {
        return couponRepository.findByUserIdAndCouponId(userId, couponId)
                               .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 입니다." + couponId));
    }
}
