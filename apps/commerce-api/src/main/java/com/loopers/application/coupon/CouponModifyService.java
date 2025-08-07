package com.loopers.application.coupon;

import java.beans.Transient;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.CouponFinder;
import com.loopers.application.provided.CouponRegister;
import com.loopers.application.required.CouponRepository;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CreateCouponSpec;
import com.loopers.domain.coupon.discount.Calculator;
import com.loopers.domain.coupon.discount.DiscountServiceFactory;
import com.loopers.domain.coupon.membercoupon.MemberCoupon;
import com.loopers.domain.member.Member;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponModifyService implements CouponRegister {
    private final CouponRepository couponRepository;
    private final CouponFinder couponFinder;
    private final DiscountServiceFactory discountServiceFactory;

    @Override
    public Coupon create(CreateCouponSpec createCouponSpec) {
        Coupon coupon = Coupon.create(createCouponSpec);
        return couponRepository.create(coupon);
    }

    @Transactional
    @Override
    public Coupon useMemberCoupon(Long couponId, Member member) {
        Coupon coupon = couponFinder.findWithPessimisticLock(couponId);
        coupon.useCoupon();

        MemberCoupon memberCoupon = MemberCoupon.create(member, coupon);
        coupon.addMemberCoupon(memberCoupon);

        couponRepository.create(coupon);

        memberCoupon.useCoupon();
        return coupon;
    }

    @Override
    public BigDecimal discountPrice(Long couponId, Member member, BigDecimal totalAmount) {
        Coupon coupon = couponFinder.find(couponId);
        Calculator calculator = discountServiceFactory.getCalculator(coupon.getDiscountPolicy());
        BigDecimal discountedPrice = calculator.discount(totalAmount);
        return discountedPrice;
    }
}
