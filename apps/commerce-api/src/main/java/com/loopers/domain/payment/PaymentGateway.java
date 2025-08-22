package com.loopers.domain.payment;

import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PgPaymentRequest;

public interface PaymentGateway {
    void requestPayment(String memberId, PgPaymentRequest pgPaymentRequest);
}
