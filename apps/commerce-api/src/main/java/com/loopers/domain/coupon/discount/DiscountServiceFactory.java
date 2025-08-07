package com.loopers.domain.coupon.discount;

import com.loopers.domain.coupon.DiscountPolicy;

public interface DiscountServiceFactory {
    Calculator getCalculator(DiscountPolicy discountPolicy);
}
