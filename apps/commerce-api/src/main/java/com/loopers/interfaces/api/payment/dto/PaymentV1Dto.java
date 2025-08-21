package com.loopers.interfaces.api.payment.dto;

import java.math.BigDecimal;

import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.PaymentStatus;
import com.loopers.domain.payment.PaymentType;

public class PaymentV1Dto {
    public class Response {
        public record TransactionResponse(String transactionKey, PaymentStatus status, String reason) {
        }
    }
    public class Request {
        public record PaymentRequest(Long orderId, String cardNo, CardType cardType,
                                     BigDecimal totalAmount, PaymentType paymentType) {
        }
        public record PgPaymentRequest(String orderId, String cardNo, CardType cardType,
                                       BigDecimal amount, PaymentType paymentType, String callbackUrl) {
            public static PgPaymentRequest of(String orderId, PaymentRequest paymentRequest, String callbackUrl) {
                return new PgPaymentRequest(orderId, paymentRequest.cardNo(), paymentRequest.cardType(),
                                            paymentRequest.totalAmount(), paymentRequest.paymentType(), callbackUrl);
            }
        }
    }
}
