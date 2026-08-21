package com.loopers.application.payment;

import java.math.BigDecimal;

import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.PaymentType;

public record PaymentRequest(Long orderId, String cardNo, CardType cardType,
                             BigDecimal totalAmount, PaymentType paymentType) {
}
