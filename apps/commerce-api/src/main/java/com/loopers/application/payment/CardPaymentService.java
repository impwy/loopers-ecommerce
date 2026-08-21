package com.loopers.application.payment;

import org.springframework.stereotype.Component;

import com.loopers.application.payment.required.PaymentGateway;
import com.loopers.domain.member.Member;
import com.loopers.domain.order.Order;

import lombok.RequiredArgsConstructor;

@Component("CARD")
@RequiredArgsConstructor
public class CardPaymentService implements PaymentService {
    private final PaymentGateway paymentGateway;
    public static final String callbackUrl = "http://localhost:8080/api/v1/payments/pg-callback";

    @Override
    public void requestPayment(Order order, Member member, PaymentRequest payment) {
        PgPaymentRequest pgPaymentRequest = PgPaymentRequest.of(order.getOrderNo().value(), payment, callbackUrl);

        paymentGateway.requestPayment(member.getUserId(), pgPaymentRequest);
    }
}
