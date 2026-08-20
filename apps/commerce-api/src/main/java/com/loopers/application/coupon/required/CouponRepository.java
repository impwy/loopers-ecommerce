package com.loopers.application.coupon.required;

import java.util.Optional;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.couponusage.CouponUsage;
import com.loopers.domain.member.UserId;

import jakarta.persistence.LockModeType;

public interface CouponRepository extends Repository<Coupon, Long> {
    Coupon save(Coupon coupon);

    Optional<Coupon> findById(Long couponId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Coupon c where c.id = :couponId")
    Optional<Coupon> findWithPessimisticLock(@Param("couponId") Long couponId);

    @Query("SELECT cu FROM CouponUsage cu JOIN FETCH cu.coupon WHERE cu.member.userId =:userId AND cu.coupon.id =:couponId")
    Optional<CouponUsage> findByUserIdAndCouponId(@Param("userId") UserId userId, @Param("couponId") Long couponId);
}
