package com.loopers.application.payment;

import org.springframework.context.ApplicationEventPublisher;

import com.loopers.domain.coupon.CouponRollback;
import com.loopers.domain.inventory.InventoryRollback;
import com.loopers.domain.member.UserId;
import com.loopers.domain.order.OrderFail;
import com.loopers.domain.payment.PaymentFail;
import com.loopers.shared.stereotype.ApplicationService;

import lombok.RequiredArgsConstructor;

@ApplicationService
@RequiredArgsConstructor
public class PaymentFailureHandler {
    private final ApplicationEventPublisher eventPublisher;

    public void handle(UserId userId, String orderId) {
        eventPublisher.publishEvent(new PaymentFail(orderId));
        eventPublisher.publishEvent(new OrderFail(orderId));
        eventPublisher.publishEvent(new InventoryRollback(orderId));
        eventPublisher.publishEvent(new CouponRollback(userId, orderId));
    }
}
