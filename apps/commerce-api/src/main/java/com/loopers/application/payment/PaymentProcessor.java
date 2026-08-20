package com.loopers.application.payment;

import com.loopers.domain.payment.PaymentType;

public interface PaymentProcessor {
    PaymentService getProcessor(PaymentType paymentType);
}
