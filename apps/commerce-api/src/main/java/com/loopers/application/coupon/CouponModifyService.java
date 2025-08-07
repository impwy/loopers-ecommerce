package com.loopers.application.coupon;

import org.springframework.stereotype.Service;

import com.loopers.application.provided.CouponRegister;
import com.loopers.application.required.CouponRepository;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CreateCouponSpec;
import com.loopers.interfaces.api.coupon.dto.CouponV1Dto.Request.CreateCouponRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponModifyService implements CouponRegister {
    private final CouponRepository couponRepository;

    @Override
    public Coupon create(CreateCouponRequest couponRequest) {
        CreateCouponSpec createCouponSpec = new CreateCouponSpec(couponRequest.code(),
                                                                 couponRequest.discountPolicy(),
                                                                 couponRequest.couponType());
        Coupon coupon = Coupon.create(createCouponSpec);
        return couponRepository.create(coupon);
    }
}
