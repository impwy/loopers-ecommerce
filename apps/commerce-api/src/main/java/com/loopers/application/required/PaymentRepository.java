package com.loopers.application.required;

import java.util.Optional;

import com.loopers.domain.payment.Payments;

public interface PaymentRepository {
    Payments save(Payments payments);

    Optional<Payments> findByOrderId(String orderId);
}
