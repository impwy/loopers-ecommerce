package com.loopers.domain.payment.paymentrule;

import com.loopers.domain.payment.PaymentType;

public interface PaymentProcessor {
    PaymentService getProcessor(PaymentType paymentType);
}
