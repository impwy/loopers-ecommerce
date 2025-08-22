package com.loopers.domain.payment;

import org.springframework.stereotype.Component;

import com.loopers.domain.member.MemberId;
import com.loopers.domain.payment.feign.PgFeignClient;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PgPaymentRequest;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Response.TransactionDetailResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentGatewayImpl implements PaymentGateway {
    private final PgFeignClient pgFeignClient;

    @Override
    public void requestPayment(MemberId memberId, PgPaymentRequest pgPaymentRequest) {
        pgFeignClient.requestPayment(memberId, pgPaymentRequest);
    }

    @Override
    public TransactionDetailResponse getPaymentStatus(MemberId memberId, String transactionKey) {
        ApiResponse<TransactionDetailResponse> paymentStatusResponse = pgFeignClient.getPaymentStatus(memberId, transactionKey);
        return paymentStatusResponse.data();
    }
}
