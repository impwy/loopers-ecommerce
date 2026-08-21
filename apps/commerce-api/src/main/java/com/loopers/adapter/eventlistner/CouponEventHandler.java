package com.loopers.adapter.eventlistner;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.loopers.application.coupon.provided.CouponRegister;
import com.loopers.application.member.provided.MemberFinder;
import com.loopers.application.order.provided.OrderFinder;
import com.loopers.domain.coupon.CouponRollback;
import com.loopers.domain.coupon.CouponUsed;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.UserId;
import com.loopers.domain.order.Order;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponEventHandler {
    private final CouponRegister couponRegister;
    private final MemberFinder memberFinder;
    private final OrderFinder orderFinder;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CouponUsed event) {
        Member member = memberFinder.findWithPoint(event.userId());
        couponRegister.useMemberCoupon(event.couponId(), member);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CouponRollback couponRollback) {
        String orderId = couponRollback.orderId();
        Order order = orderFinder.findByOrderNo(orderId);
        UserId userId = couponRollback.userId();
        order.getOrderItems().forEach(orderItem -> {
            couponRegister.rollback(userId, orderItem.getCouponId());
        });
    }
}
