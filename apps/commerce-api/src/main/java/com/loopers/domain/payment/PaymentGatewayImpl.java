package com.loopers.domain.payment;

import org.springframework.stereotype.Component;

import com.loopers.domain.member.MemberId;
import com.loopers.domain.payment.feign.PgFeignClient;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PgPaymentRequest;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Response.TransactionDetailResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentGatewayImpl implements PaymentGateway {
    private final PgFeignClient pgFeignClient;

    @CircuitBreaker(name = "pgCircuit", fallbackMethod = "fallbackPgPayment")
    @Override
    public void requestPayment(MemberId memberId, PgPaymentRequest pgPaymentRequest) {
        pgFeignClient.requestPayment(memberId, pgPaymentRequest);
    }

    @CircuitBreaker(name = "pgCircuit", fallbackMethod = "fallbackPgPayment")
    @Override
    public TransactionDetailResponse getPaymentDetailResponse(MemberId memberId, String transactionKey) {
        ApiResponse<TransactionDetailResponse> paymentStatusResponse = pgFeignClient.getPaymentStatus(memberId,
                                                                                                      transactionKey);
        return paymentStatusResponse.data();
    }

    public void fallbackPgPayment(PaymentRequest paymentRequest, Throwable throwable) {
        log.warn("pg 요청에 실패했습니다. : {}", throwable.getMessage());
        // 다른 PG사에 연결한다.
    }
}
