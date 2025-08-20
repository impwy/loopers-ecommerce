package com.loopers.domain.payment;

import java.math.BigDecimal;

public record CreatePaymentSpec(Long orderId, Long memberId, String transactionKey,
                                CardType cardType, String cardNo, BigDecimal totalAmount,
                                PaymentType paymentType) {
}
