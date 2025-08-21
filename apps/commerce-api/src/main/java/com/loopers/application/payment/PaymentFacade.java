package com.loopers.application.payment;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.MemberFinder;
import com.loopers.application.provided.OrderFinder;
import com.loopers.application.provided.PaymentFinder;
import com.loopers.application.provided.PaymentRegister;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;
import com.loopers.domain.order.Order;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentFacade {
    private final PaymentRegister paymentRegister;
    private final PaymentFinder paymentFinder;
    private final MemberFinder memberFinder;
    private final OrderFinder orderFinder;

    // 결제 요청
    @Transactional
    public void requestPayment(MemberId memberId, PaymentRequest paymentRequest) {
        Member member = memberFinder.findByMemberId(memberId);
        Order order = orderFinder.find(paymentRequest.orderId());
        paymentRegister.createPayment(member.getId(), paymentRequest);
        paymentRegister.requestPayment(order.getOrderNo().value(), memberId, paymentRequest);
    }

    // 결제 콜백
}
