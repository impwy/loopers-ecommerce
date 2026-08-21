package com.loopers.application.payment.required;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.loopers.domain.payment.Payments;

public interface PaymentRepository extends Repository<Payments, Long> {
    Payments save(Payments payments);

    List<Payments> saveAll(Iterable<Payments> payments);

    Optional<Payments> findByOrderId(String orderId);

    List<Payments> findAllByOrderId(String orderId);
}
