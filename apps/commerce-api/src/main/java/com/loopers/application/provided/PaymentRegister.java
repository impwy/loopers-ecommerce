package com.loopers.application.provided;

import com.loopers.domain.member.MemberId;
import com.loopers.domain.payment.Payments;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Response.TransactionDetailResponse;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Response.TransactionResponse;

public interface PaymentRegister {
    Payments createPayment(Long memberId, PaymentRequest paymentRequest);

    void requestPayment(String orderId, MemberId memberId, PaymentRequest paymentRequest);

    TransactionDetailResponse getPaymentDetailResponse(MemberId memberId, TransactionResponse transactionResponse);

    void successPayment(String orderId);

    void failPayment(String orderId);
}
