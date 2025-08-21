package com.loopers.domain.payment.paymentrule;

import org.springframework.stereotype.Component;

import com.loopers.domain.member.MemberId;
import com.loopers.infrastructure.payment.feign.PgFeignClient;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PgPaymentRequest;

import lombok.RequiredArgsConstructor;

@Component("CARD")
@RequiredArgsConstructor
public class CardPaymentService implements PaymentService {
    private final PgFeignClient pgFeignClient;
    public static final String callbackUrl = "http://localhost:8080/api/v1/payments/pg-callback";

    @Override
    public <T> void pay(String orderId, MemberId memberId, T payment) {
        PaymentRequest paymentRequest = (PaymentRequest) payment;
        PgPaymentRequest pgPaymentRequest = PgPaymentRequest.of(orderId, paymentRequest, callbackUrl);
        pgFeignClient.paymentRequest(memberId.memberId(), pgPaymentRequest);
    }
}
