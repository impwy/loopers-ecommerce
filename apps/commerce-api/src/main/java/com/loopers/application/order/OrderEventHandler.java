package com.loopers.application.order;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.loopers.application.provided.OrderRegister;
import com.loopers.domain.order.OrderFail;
import com.loopers.domain.payment.PaymentSuccess;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderEventHandler {
    private final OrderRegister orderRegister;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void failOrder(OrderFail orderFail) {
        String orderId = orderFail.orderId();
        orderRegister.failOrder(orderId);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void successOrder(PaymentSuccess paymentSuccess) {
        orderRegister.successOrder(paymentSuccess.orderId());
    }
}
