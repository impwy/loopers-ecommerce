package com.loopers.application.coupon;

import java.math.BigDecimal;

import com.loopers.application.coupon.provided.CouponFinder;
import com.loopers.application.coupon.provided.CouponRegister;
import com.loopers.application.coupon.required.CouponRepository;
import com.loopers.application.coupon.required.MemberCouponRepository;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponStatus;
import com.loopers.domain.coupon.CreateCouponSpec;
import com.loopers.domain.coupon.discount.Calculator;
import com.loopers.domain.coupon.discount.DiscountServiceFactory;
import com.loopers.domain.couponusage.CouponUsage;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.UserId;
import com.loopers.shared.error.CoreException;
import com.loopers.shared.error.ErrorType;
import com.loopers.shared.stereotype.ApplicationValidService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationValidService
@RequiredArgsConstructor
public class CouponModifyService implements CouponRegister {
    private final CouponRepository couponRepository;
    private final CouponFinder couponFinder;
    private final MemberCouponRepository memberCouponRepository;
    private final DiscountServiceFactory discountServiceFactory;

    @Override
    public Coupon create(CreateCouponSpec createCouponSpec) {
        Coupon coupon = Coupon.create(createCouponSpec);
        return couponRepository.save(coupon);
    }

    @Transactional
    @Override
    public Coupon useMemberCoupon(Long couponId, Member member) {
        Coupon coupon = couponFinder.findWithPessimisticLock(couponId);
        coupon.useCoupon();

        if (memberCouponRepository.existsByMemberAndCoupon(member, coupon)) {
            throw new CoreException(ErrorType.CONFLICT, "이미 발급 된 쿠폰입니다.");
        }
        CouponUsage couponUsage = CouponUsage.create(member, coupon);

        if (couponUsage.getStatus().equals(CouponStatus.USED)) {
            throw new CoreException(ErrorType.CONFLICT, "이미 사용된 쿠폰입니다.");
        }

        coupon.addMemberCoupon(couponUsage);

        couponRepository.save(coupon);

        couponUsage.useCoupon();
        return coupon;
    }

    @Override
    public BigDecimal discountPrice(Long couponId, Member member, BigDecimal totalAmount) {
        Coupon coupon = couponFinder.find(couponId);
        Calculator calculator = discountServiceFactory.getCalculator(coupon.getDiscountPolicy());
        BigDecimal discountedPrice = calculator.discount(totalAmount);
        return discountedPrice;
    }

    @Transactional
    @Override
    public void rollback(UserId userId, Long couponId) {
        CouponUsage couponUsage = couponFinder.findMemberCoupon(userId, couponId);
        couponUsage.rollback();
        Coupon coupon = couponFinder.find(couponId);
        coupon.rollback();
    }
}
