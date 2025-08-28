package com.loopers.domain.payment;

import com.loopers.domain.member.MemberId;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PgPaymentRequest;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Response.TransactionDetailResponse;

public interface PaymentGateway {
    void requestPayment(MemberId memberId, PgPaymentRequest pgPaymentRequest);

    TransactionDetailResponse getPaymentDetailResponse(MemberId memberId, String transactionKey);
}
