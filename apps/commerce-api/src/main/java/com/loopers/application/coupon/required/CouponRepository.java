package com.loopers.application.coupon.required;

import java.util.Optional;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.membercoupon.MemberCoupon;
import com.loopers.domain.member.MemberId;

import jakarta.persistence.LockModeType;

public interface CouponRepository extends Repository<Coupon, Long> {
    Coupon save(Coupon coupon);

    Optional<Coupon> findById(Long couponId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Coupon c where c.id = :couponId")
    Optional<Coupon> findWithPessimisticLock(@Param("couponId") Long couponId);

    @Query("SELECT mc FROM MemberCoupon mc JOIN FETCH mc.coupon WHERE mc.member.memberId =:memberId AND mc.coupon.id =:couponId")
    Optional<MemberCoupon> findByMemberIdAndCouponId(@Param("memberId") MemberId memberId, @Param("couponId") Long couponId);
}
