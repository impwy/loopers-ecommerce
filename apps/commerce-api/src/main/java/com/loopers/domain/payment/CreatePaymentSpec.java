package com.loopers.domain.payment;

import java.math.BigDecimal;

import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;

public record CreatePaymentSpec(String orderId, Long memberId, String transactionKey,
                                CardType cardType, String cardNo, BigDecimal totalAmount,
                                PaymentType paymentType) {
    public static CreatePaymentSpec of(String orderId, Long memberId, PaymentRequest paymentRequest) {
        return new CreatePaymentSpec(orderId, memberId, null, paymentRequest.cardType(),
                                     paymentRequest.cardNo(), paymentRequest.totalAmount(), paymentRequest.paymentType());
    }
}
