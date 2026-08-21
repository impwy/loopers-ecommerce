package com.loopers.application.payment;

import java.math.BigDecimal;

import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.PaymentStatus;

public record PaymentDetailResult(String transactionKey, String orderId, CardType cardType,
                                  String cardNo, BigDecimal amount, PaymentStatus status, String reason) {
}
