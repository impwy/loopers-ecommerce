package com.loopers.domain.coupon.discount;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.loopers.domain.coupon.DiscountPolicy;

@Component
public class DiscountService implements DiscountServiceFactory {
    Map<DiscountPolicy, Calculator> calculatorMap;

    public DiscountService(Map<String, Calculator> calculators) {
        this.calculatorMap = calculators.entrySet().stream()
                                        .collect(Collectors.toMap(entry -> DiscountPolicy.getDiscount(entry.getKey()),
                                                                  Entry::getValue));
    }

    public Calculator getCalculator(DiscountPolicy discountPolicy) {
        Calculator calculator = calculatorMap.get(discountPolicy);
        if (calculator == null) {
            throw new IllegalArgumentException("지원하지 않는 할인 쿠폰 정책입니다: " + discountPolicy);
        }
        return calculator;
    }
}
