package com.loopers.application.coupon;

import org.springframework.stereotype.Component;

import com.loopers.application.coupon.provided.CouponRegister;
import com.loopers.domain.coupon.discount.DiscountServiceFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponFacade {
    private final CouponRegister couponRegister;
    private final DiscountServiceFactory discountServiceFactory;


}
