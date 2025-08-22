package com.loopers.domain.payment;

import org.springframework.stereotype.Component;

import com.loopers.domain.payment.feign.PgFeignClient;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PgPaymentRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentGatewayImpl implements PaymentGateway {
    private final PgFeignClient pgFeignClient;

    @Override
    public void requestPayment(String memberId, PgPaymentRequest pgPaymentRequest) {
        pgFeignClient.requestPayment(memberId, pgPaymentRequest);
    }
}
