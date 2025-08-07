package com.loopers.infrastructure.coupon;

import org.springframework.stereotype.Component;

import com.loopers.application.required.CouponRepository;
import com.loopers.domain.coupon.Coupon;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {
    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Coupon create(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }
}
