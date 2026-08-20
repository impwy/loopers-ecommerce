package com.loopers.application.payment;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.loopers.adapter.webapi.payment.dto.PaymentV1Dto.Request.PaymentRequest;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.PointUsageRequest;
import com.loopers.domain.order.Order;
import com.loopers.domain.payment.PaymentSuccess;

import lombok.RequiredArgsConstructor;

@Component("POINT")
@RequiredArgsConstructor
public class PointPaymentService implements PaymentService {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void requestPayment(Order order, Member member, PaymentRequest payment) {
        eventPublisher.publishEvent(new PointUsageRequest(member.getUserId(), payment.totalAmount()));
        eventPublisher.publishEvent(new PaymentSuccess(order.getOrderNo().value()));
    }
}
