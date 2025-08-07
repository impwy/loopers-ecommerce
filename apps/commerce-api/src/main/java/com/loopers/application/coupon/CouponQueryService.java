package com.loopers.application.coupon;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.CouponFinder;
import com.loopers.application.provided.CouponRegister;
import com.loopers.application.required.CouponRepository;
import com.loopers.domain.coupon.Coupon;

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
}
