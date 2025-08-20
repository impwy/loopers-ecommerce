package com.loopers.domain.payment;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PaymentType {
    CARD("카드 결제"),
    CASH("현금 결제"),
    POINT("포인트 결제")
    ;
    
    private final String description;
}
