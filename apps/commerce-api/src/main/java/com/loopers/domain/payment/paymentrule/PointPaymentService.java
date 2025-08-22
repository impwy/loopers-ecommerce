package com.loopers.domain.payment.paymentrule;

import org.springframework.stereotype.Component;

import com.loopers.domain.member.MemberId;

import lombok.RequiredArgsConstructor;

@Component("POINT")
@RequiredArgsConstructor
public class PointPaymentService implements PaymentService {

    @Override
    public <T> void requestPayment(String orderId, MemberId memberId, T payment) {
        // 포인트를 차감한다.
    }
}
