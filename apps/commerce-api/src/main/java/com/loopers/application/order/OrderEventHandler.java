package com.loopers.application.order;

import com.loopers.domain.payment.PaymentSuccess;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.loopers.application.provided.OrderRegister;
import com.loopers.domain.order.OrderFail;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderEventHandler {
    private final OrderRegister orderRegister;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void failOrder(OrderFail orderFail) {
        String orderId = orderFail.orderId();
        orderRegister.failOrder(orderId);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void successOrder(PaymentSuccess paymentSuccess) {
        orderRegister.successOrder(paymentSuccess.orderId());
    }
}
