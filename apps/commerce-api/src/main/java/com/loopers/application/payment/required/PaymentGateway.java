package com.loopers.application.payment.required;

import com.loopers.application.payment.PaymentDetailResult;
import com.loopers.application.payment.PgPaymentRequest;
import com.loopers.domain.member.UserId;

public interface PaymentGateway {
    void requestPayment(UserId userId, PgPaymentRequest pgPaymentRequest);

    PaymentDetailResult getPaymentDetailResponse(UserId userId, String transactionKey);
}
