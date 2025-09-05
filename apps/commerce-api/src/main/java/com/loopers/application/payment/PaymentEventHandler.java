package com.loopers.application.payment;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.loopers.application.provided.PaymentRegister;
import com.loopers.domain.payment.PaymentFail;
import com.loopers.domain.payment.PaymentSuccess;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentEventHandler {
    private final PaymentRegister paymentRegister;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void failPayment(PaymentFail paymentFail) {
        paymentRegister.failPayment(paymentFail.orderId());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void successPayment(PaymentSuccess paymentSuccess) {
        paymentRegister.successPayment(paymentSuccess.orderId());
    }
}
