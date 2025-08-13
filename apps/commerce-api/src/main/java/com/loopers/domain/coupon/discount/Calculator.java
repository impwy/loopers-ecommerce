package com.loopers.domain.coupon.discount;

import java.math.BigDecimal;

public interface Calculator {
    BigDecimal discount(BigDecimal totalAmount);
}
