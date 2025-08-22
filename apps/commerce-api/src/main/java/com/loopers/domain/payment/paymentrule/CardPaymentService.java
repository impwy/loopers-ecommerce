package com.loopers.domain.payment.paymentrule;

import org.springframework.stereotype.Component;

import com.loopers.domain.member.MemberId;
import com.loopers.domain.payment.PaymentGateway;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PgPaymentRequest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("CARD")
@RequiredArgsConstructor
public class CardPaymentService implements PaymentService {
    private final PaymentGateway paymentGateway;
    public static final String callbackUrl = "http://localhost:8080/api/v1/payments/pg-callback";

    @CircuitBreaker(name = "pgCircuit", fallbackMethod = "fallbackPgPayment")
    @Override
    public <T> void requestPayment(String orderId, MemberId memberId, T payment) {
        PaymentRequest paymentRequest = (PaymentRequest) payment;
        PgPaymentRequest pgPaymentRequest = PgPaymentRequest.of(orderId, paymentRequest, callbackUrl);

        paymentGateway.requestPayment(memberId.memberId(), pgPaymentRequest);
    }

    public void fallbackPgPayment(PaymentRequest paymentRequest, Throwable throwable) {
        log.warn("pg 요청에 실패했습니다. : {}", throwable.getMessage());
        // 다른 PG사에 연결한다.
    }
}
