package com.loopers.application.required;

import java.util.List;
import java.util.Optional;

import com.loopers.domain.payment.Payments;

public interface PaymentRepository {
    Payments save(Payments payments);

    List<Payments> saveAll(List<Payments> payments);

    Optional<Payments> findByOrderId(String orderId);

    List<Payments> findALlByOrderId(String orderId);
}
