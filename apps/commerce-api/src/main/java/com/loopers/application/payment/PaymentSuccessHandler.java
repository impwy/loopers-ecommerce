package com.loopers.application.payment;

import org.springframework.context.ApplicationEventPublisher;

import com.loopers.domain.payment.PaymentSuccess;
import com.loopers.shared.stereotype.ApplicationService;

import lombok.RequiredArgsConstructor;

@ApplicationService
@RequiredArgsConstructor
public class PaymentSuccessHandler {
    private final ApplicationEventPublisher eventPublisher;

    public void handle(String orderId) {
        eventPublisher.publishEvent(new PaymentSuccess(orderId));
    }
}
