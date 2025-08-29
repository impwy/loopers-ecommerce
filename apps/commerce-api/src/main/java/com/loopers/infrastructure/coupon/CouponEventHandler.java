package com.loopers.infrastructure.coupon;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.loopers.application.provided.CouponRegister;
import com.loopers.application.provided.MemberFinder;
import com.loopers.application.provided.OrderFinder;
import com.loopers.domain.coupon.CouponRollback;
import com.loopers.domain.coupon.CouponUsed;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;
import com.loopers.domain.order.Order;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponEventHandler {
    private final CouponRegister couponRegister;
    private final MemberFinder memberFinder;
    private final OrderFinder orderFinder;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CouponUsed event) {
        Member member = memberFinder.findByMemberId(event.memberId());
        couponRegister.useMemberCoupon(event.couponId(), member);
    }

    @Async
    @EventListener
    public void handle(CouponRollback couponRollback) {
        String orderId = couponRollback.orderId();
        Order order = orderFinder.findByOrderNo(orderId);
        MemberId memberId = couponRollback.memberId();
        order.getOrderItems().forEach(orderItem -> {
            couponRegister.rollback(memberId, orderItem.getCouponId());
        });
    }
}
