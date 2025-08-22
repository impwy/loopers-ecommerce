package com.loopers.domain.payment;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PaymentStatus {
    PENDING("결제 대기"),
    SUCCESS("결제 성공"),
    FAILED("결제 실패")
    ;
    
    private final String description;
}
