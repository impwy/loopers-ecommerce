package com.loopers.domain.coupon.discount;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component("AMOUNT")
public class AmountMoneyCalculator implements Calculator {
    private static final BigDecimal AMOUNT_MONEY = BigDecimal.valueOf(1000);

    @Override
    public BigDecimal discount(BigDecimal totalAmount) {
        return totalAmount.subtract(AMOUNT_MONEY);
    }
}
