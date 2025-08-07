package com.loopers.application.coupon;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.CouponRegister;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.discount.Calculator;
import com.loopers.domain.coupon.discount.DiscountServiceFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponFacade {
    private final CouponRegister couponRegister;
    private final DiscountServiceFactory discountServiceFactory;


}
