package com.loopers.application.payment.provided;

import com.loopers.application.payment.PaymentCallbackRequest;
import com.loopers.application.payment.PaymentDetailResult;
import com.loopers.application.payment.PaymentRequest;
import com.loopers.domain.member.UserId;
import com.loopers.domain.payment.Payments;

public interface PaymentRegister {
    Payments createPayment(Long memberId, PaymentRequest paymentRequest);

    PaymentDetailResult getPaymentDetailResponse(UserId userId, PaymentCallbackRequest callbackRequest);

    void successPayment(String orderId);

    void failPayment(String orderId);
}
