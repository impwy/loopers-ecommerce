package com.loopers.domain.payment;

import java.math.BigDecimal;

import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;

public record CreatePaymentSpec(Long orderId, Long memberId, String transactionKey,
                                CardType cardType, String cardNo, BigDecimal totalAmount,
                                PaymentType paymentType) {
    public static CreatePaymentSpec of(Long memberId, PaymentRequest paymentRequest) {
        return new CreatePaymentSpec(paymentRequest.orderId(), memberId, null, paymentRequest.cardType(),
                                     paymentRequest.cardNo(), paymentRequest.totalAmount(), paymentRequest.paymentType());
    }
}
