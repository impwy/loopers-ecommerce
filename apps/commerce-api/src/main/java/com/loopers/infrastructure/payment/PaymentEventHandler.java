package com.loopers.infrastructure.payment;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.PaymentRegister;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentEventHandler {
    private final PaymentRegister paymentRegister;
}
