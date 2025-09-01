package com.loopers.application.payment;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.loopers.domain.coupon.CouponRollback;
import com.loopers.domain.inventory.InventoryRollback;
import com.loopers.domain.member.MemberId;
import com.loopers.domain.order.OrderFail;
import com.loopers.domain.payment.PaymentFail;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentFailureHandler {
    private final ApplicationEventPublisher eventPublisher;

    public void handle(MemberId memberId, String orderId) {
        eventPublisher.publishEvent(new PaymentFail(orderId));
        eventPublisher.publishEvent(new OrderFail(orderId));
        eventPublisher.publishEvent(new InventoryRollback(orderId));
        eventPublisher.publishEvent(new CouponRollback(memberId, orderId));
    }
}
