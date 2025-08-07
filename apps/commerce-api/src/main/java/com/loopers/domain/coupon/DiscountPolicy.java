package com.loopers.domain.coupon;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum DiscountPolicy {
    AMOUNT,
    RATE;

    public static DiscountPolicy getDiscount(String discountType) {
        return getDiscountMap().get(discountType);
    }

    private static Map<String, DiscountPolicy> getDiscountMap() {
        return Arrays.stream(values())
                     .collect(Collectors.toMap(Enum::name, Function.identity()));
    }
}
