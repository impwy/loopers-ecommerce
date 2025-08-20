package com.loopers.domain.payment;

import java.math.BigDecimal;

public record CreatePaymentSpec(Long orderId, Long memberId, String transactionKey,
                                CardType cardType, String cardNo, BigDecimal totalAmount,
                                PaymentType paymentType) {
    public static CreatePaymentSpec of(Long orderId, Long memberId, CardType cardType,
                                       String cardNo, BigDecimal totalAmount, PaymentType paymentType) {
        return new CreatePaymentSpec(orderId, memberId, null, cardType, cardNo, totalAmount, paymentType);
    }
}
