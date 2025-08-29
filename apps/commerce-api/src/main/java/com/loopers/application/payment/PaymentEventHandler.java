package com.loopers.application.payment;

import com.loopers.domain.payment.PaymentSuccess;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.loopers.application.provided.PaymentRegister;
import com.loopers.domain.payment.PaymentFail;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentEventHandler {
    private final PaymentRegister paymentRegister;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void failPayment(PaymentFail paymentFail) {
        paymentRegister.failPayment(paymentFail.orderId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void successPayment(PaymentSuccess paymentSuccess) {
        paymentRegister.successPayment(paymentSuccess.orderId());
    }
}
