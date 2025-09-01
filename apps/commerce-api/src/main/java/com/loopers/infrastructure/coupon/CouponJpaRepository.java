package com.loopers.infrastructure.coupon;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.membercoupon.MemberCoupon;
import com.loopers.domain.member.MemberId;

import jakarta.persistence.LockModeType;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Coupon c where c.id = :couponId")
    Optional<Coupon> findWithPessimisticLock(@Param("couponId") Long couponId);

    @Query("SELECT mc FROM MemberCoupon mc JOIN FETCH mc.coupon WHERE mc.member.memberId =:memberId AND mc.coupon.id =:couponId")
    Optional<MemberCoupon> findByMemberIdAndCouponId(@Param("memberId") MemberId memberId, @Param("couponId") Long couponId);
}
