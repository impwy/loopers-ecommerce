package com.loopers.adapter.integration.feign;

import org.springframework.stereotype.Component;

import com.loopers.application.payment.required.PaymentGateway;
import com.loopers.domain.member.UserId;
import com.loopers.adapter.webapi.ApiResponse;
import com.loopers.adapter.webapi.payment.dto.PaymentV1Dto.Request.PgPaymentRequest;
import com.loopers.adapter.webapi.payment.dto.PaymentV1Dto.Response.TransactionDetailResponse;
import com.loopers.shared.error.CoreException;
import com.loopers.shared.error.ErrorType;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentGatewayAdapter implements PaymentGateway {
    private final PgFeignClient pgFeignClient;

    @CircuitBreaker(name = "pgCircuit", fallbackMethod = "fallbackPgPayment")
    @Override
    public void requestPayment(UserId userId, PgPaymentRequest pgPaymentRequest) {
        try {
            pgFeignClient.requestPayment(userId.userId(), pgPaymentRequest);
        } catch (FeignException e) {
            if (e.status() >= 400 && e.status() < 500) {
                throw new CoreException(ErrorType.BAD_REQUEST, e.contentUTF8());
            }
            throw e;
        }
    }

    @CircuitBreaker(name = "pgCircuit")
    @Override
    public TransactionDetailResponse getPaymentDetailResponse(UserId userId, String transactionKey) {
        ApiResponse<TransactionDetailResponse> paymentStatusResponse =
                pgFeignClient.getPaymentStatus(userId.userId(), transactionKey);
        return paymentStatusResponse.data();
    }

    public void fallbackPgPayment(UserId userId, PgPaymentRequest pgPaymentRequest, Throwable throwable) {
        log.warn("pg 요청에 실패했습니다. : {}", throwable.getMessage());
        // TODO: 다른 PG사에 연결하는 로직을 추가할 수 있습니다.
    }
}
