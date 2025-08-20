package com.loopers.interfaces.api.payment.dto;

import java.math.BigDecimal;

import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.PaymentType;

public class PaymentV1Dto {
    public class Request {
        public record PaymentRequest(Long orderId, String cardNo, CardType cardType,
                                     BigDecimal totalAmount, PaymentType paymentType) {

        }
    }
}
