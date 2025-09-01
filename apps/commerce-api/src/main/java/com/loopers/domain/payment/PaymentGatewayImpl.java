package com.loopers.domain.payment;

import org.springframework.stereotype.Component;

import com.loopers.domain.member.MemberId;
import com.loopers.infrastructure.payment.feign.PgFeignClient;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PgPaymentRequest;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Response.TransactionDetailResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

import feign.FeignException;
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
        try {
            pgFeignClient.requestPayment(memberId.memberId(), pgPaymentRequest);
        } catch (FeignException e) {
            if (e.status() >= 400 && e.status() < 500) {
                throw new CoreException(ErrorType.BAD_REQUEST, e.contentUTF8());
            }
            throw e;
        }
    }

    @CircuitBreaker(name = "pgCircuit")
    @Override
    public TransactionDetailResponse getPaymentDetailResponse(MemberId memberId, String transactionKey) {
        ApiResponse<TransactionDetailResponse> paymentStatusResponse =
                pgFeignClient.getPaymentStatus(memberId.memberId(), transactionKey);
        return paymentStatusResponse.data();
    }

    public void fallbackPgPayment(MemberId memberId, PgPaymentRequest pgPaymentRequest, Throwable throwable) {
        log.warn("pg 요청에 실패했습니다. : {}", throwable.getMessage());
        // TODO: 다른 PG사에 연결하는 로직을 추가할 수 있습니다.
    }
}
