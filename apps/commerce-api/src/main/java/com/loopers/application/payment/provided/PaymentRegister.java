package com.loopers.application.payment.provided;

import com.loopers.adapter.webapi.payment.dto.PaymentV1Dto.Request.PaymentRequest;
import com.loopers.adapter.webapi.payment.dto.PaymentV1Dto.Response.TransactionDetailResponse;
import com.loopers.adapter.webapi.payment.dto.PaymentV1Dto.Response.TransactionResponse;
import com.loopers.domain.member.UserId;
import com.loopers.domain.payment.Payments;

public interface PaymentRegister {
    Payments createPayment(Long memberId, PaymentRequest paymentRequest);

    TransactionDetailResponse getPaymentDetailResponse(UserId userId, TransactionResponse transactionResponse);

    void successPayment(String orderId);

    void failPayment(String orderId);
}
