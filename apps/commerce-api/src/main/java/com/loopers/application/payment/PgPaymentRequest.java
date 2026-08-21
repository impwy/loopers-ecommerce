package com.loopers.application.payment;

import java.math.BigDecimal;

import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.PaymentType;

public record PgPaymentRequest(String orderId, String cardNo, CardType cardType,
                               BigDecimal amount, PaymentType paymentType, String callbackUrl) {
    public static PgPaymentRequest of(String orderId, PaymentRequest paymentRequest, String callbackUrl) {
        return new PgPaymentRequest(orderId, paymentRequest.cardNo(), paymentRequest.cardType(),
                                    paymentRequest.totalAmount(), paymentRequest.paymentType(), callbackUrl);
    }
}
