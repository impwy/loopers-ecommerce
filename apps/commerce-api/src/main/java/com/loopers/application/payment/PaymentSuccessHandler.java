package com.loopers.application.payment;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.loopers.domain.payment.PaymentSuccess;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentSuccessHandler {
    private final ApplicationEventPublisher eventPublisher;

    public void handle(String orderId) {
        eventPublisher.publishEvent(new PaymentSuccess(orderId));
    }
}
