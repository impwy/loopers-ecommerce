package com.loopers.domain.coupon.discount;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component("RATE")
public class RateMoneyCalculator implements Calculator {
    private static final BigDecimal FIXED_RATE = new BigDecimal("0.5");

    @Override
    public BigDecimal discount(BigDecimal totalAmount) {
        return totalAmount.subtract(totalAmount.multiply(FIXED_RATE));
    }
}
