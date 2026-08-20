package com.loopers.domain.payment;

import com.loopers.domain.member.UserId;
import com.loopers.adapter.webapi.payment.dto.PaymentV1Dto.Request.PgPaymentRequest;
import com.loopers.adapter.webapi.payment.dto.PaymentV1Dto.Response.TransactionDetailResponse;

public interface PaymentGateway {
    void requestPayment(UserId userId, PgPaymentRequest pgPaymentRequest);

    TransactionDetailResponse getPaymentDetailResponse(UserId userId, String transactionKey);
}
