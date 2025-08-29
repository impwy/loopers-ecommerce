package com.loopers.application.order;

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
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void failOrder(OrderFail orderFail) {
        String orderId = orderFail.orderId();
        orderRegister.failOrder(orderId);
    }
}
