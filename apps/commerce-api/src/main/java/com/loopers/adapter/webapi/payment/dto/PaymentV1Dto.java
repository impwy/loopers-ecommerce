package com.loopers.adapter.webapi.payment.dto;

import java.math.BigDecimal;

import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.PaymentStatus;

public class PaymentV1Dto {
    public record TransactionResponse(String transactionKey, PaymentStatus status, String reason) {}

    public record TransactionDetailResponse(String transactionKey, String orderId, CardType cardType,
                                            String cardNo, BigDecimal amount, PaymentStatus status, String reason) {}
}
