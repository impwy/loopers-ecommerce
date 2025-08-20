package com.loopers.application.required;

import com.loopers.domain.payment.Payments;

public interface PaymentRepository {
    Payments save(Payments payments);
}
