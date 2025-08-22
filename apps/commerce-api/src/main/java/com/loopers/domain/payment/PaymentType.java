package com.loopers.domain.payment;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentType {
    CARD("카드 결제"),
    CASH("현금 결제"),
    POINT("포인트 결제")
    ;
    
    private final String description;

    public static PaymentType of(String name) {
        return getPaymentTypeMap().get(name);
    }

    private static Map<String, PaymentType> getPaymentTypeMap() {
        return Arrays.stream(values())
                     .collect(Collectors.toMap(Enum::name, Function.identity()));
    }
}
