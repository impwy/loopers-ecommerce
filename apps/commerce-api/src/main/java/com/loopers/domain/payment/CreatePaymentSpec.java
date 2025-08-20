package com.loopers.domain.payment;

import java.math.BigDecimal;

public record CreatePaymentSpec(Long orderId, Long memberId, String paymentKey, String transactionKey,
                                CardType cardType, String cardNo, BigDecimal totalAmount,
                                PaymentType paymentType, PaymentStatus paymentStatus) {
}
