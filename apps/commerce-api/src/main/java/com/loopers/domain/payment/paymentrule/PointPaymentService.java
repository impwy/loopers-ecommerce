package com.loopers.domain.payment.paymentrule;

import com.loopers.domain.member.Member;
import com.loopers.domain.member.point.PointUsageRequest;
import com.loopers.domain.order.Order;
import com.loopers.domain.payment.PaymentSuccess;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component("POINT")
@RequiredArgsConstructor
public class PointPaymentService implements PaymentService {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void requestPayment(Order order, Member member, PaymentRequest payment) {
        eventPublisher.publishEvent(new PointUsageRequest(member.getMemberId(), payment.totalAmount()));
        eventPublisher.publishEvent(new PaymentSuccess(order.getOrderNo().value()));
    }
}
