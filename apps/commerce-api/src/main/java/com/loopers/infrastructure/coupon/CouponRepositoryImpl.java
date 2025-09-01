package com.loopers.infrastructure.coupon;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.loopers.application.required.CouponRepository;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.membercoupon.MemberCoupon;
import com.loopers.domain.member.MemberId;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {
    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Coupon create(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public Optional<Coupon> find(Long couponId) {
        return couponJpaRepository.findById(couponId);
    }

    @Override
    public Optional<Coupon> findWithPessimisticLock(Long couponId) {
        return couponJpaRepository.findWithPessimisticLock(couponId);
    }

    @Override
    public Optional<MemberCoupon> findByMemberIdAndCouponId(MemberId memberId, Long couponId) {
        return couponJpaRepository.findByMemberIdAndCouponId(memberId, couponId);
    }
}
