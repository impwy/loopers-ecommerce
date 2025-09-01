package com.loopers.application.coupon;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.CouponFinder;
import com.loopers.application.provided.CouponRegister;
import com.loopers.application.required.CouponRepository;
import com.loopers.application.required.MemberCouponRepository;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CreateCouponSpec;
import com.loopers.domain.coupon.discount.Calculator;
import com.loopers.domain.coupon.discount.DiscountServiceFactory;
import com.loopers.domain.coupon.membercoupon.CouponStatus;
import com.loopers.domain.coupon.membercoupon.MemberCoupon;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponModifyService implements CouponRegister {
    private final CouponRepository couponRepository;
    private final CouponFinder couponFinder;
    private final MemberCouponRepository memberCouponRepository;
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

        if (memberCouponRepository.hasMemberCoupon(member, coupon)) {
            throw new CoreException(ErrorType.CONFLICT, "이미 발급 된 쿠폰입니다.");
        }
        MemberCoupon memberCoupon = MemberCoupon.create(member, coupon);

        if (memberCoupon.getCouponStatus().equals(CouponStatus.USED)) {
            throw new CoreException(ErrorType.CONFLICT, "이미 사용된 쿠폰입니다.");
        }

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

    @Transactional
    @Override
    public void rollback(MemberId memberId, Long couponId) {
        MemberCoupon memberCoupon = couponFinder.findMemberCoupon(memberId, couponId);
        memberCoupon.rollback();
        Coupon coupon = couponFinder.find(couponId);
        coupon.rollback();
    }
}
