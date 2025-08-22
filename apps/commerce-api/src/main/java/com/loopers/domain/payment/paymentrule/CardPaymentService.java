package com.loopers.domain.payment.paymentrule;

import org.springframework.stereotype.Component;

import com.loopers.domain.member.MemberId;
import com.loopers.domain.payment.PaymentGateway;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PgPaymentRequest;

import lombok.RequiredArgsConstructor;

@Component("CARD")
@RequiredArgsConstructor
public class CardPaymentService implements PaymentService {
    private final PaymentGateway paymentGateway;
    public static final String callbackUrl = "http://localhost:8080/api/v1/payments/pg-callback";

    @Override
    public void requestPayment(String orderId, MemberId memberId, PaymentRequest payment) {
        PaymentRequest paymentRequest = payment;
        PgPaymentRequest pgPaymentRequest = PgPaymentRequest.of(orderId, paymentRequest, callbackUrl);

        paymentGateway.requestPayment(memberId, pgPaymentRequest);
    }
}
