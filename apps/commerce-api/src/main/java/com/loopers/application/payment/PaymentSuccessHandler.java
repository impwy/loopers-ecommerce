package com.loopers.application.payment;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.OrderRegister;
import com.loopers.application.provided.PaymentRegister;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentSuccessHandler {
    private final PaymentRegister paymentRegister;
    private final OrderRegister orderRegister;

    public void handle(String orderId) {
        paymentRegister.successPayment(orderId);
        orderRegister.successOrder(orderId);
    }
}
