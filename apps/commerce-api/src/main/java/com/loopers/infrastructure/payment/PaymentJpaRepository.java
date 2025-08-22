package com.loopers.infrastructure.payment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.payment.Payments;

public interface PaymentJpaRepository extends JpaRepository<Payments, Long> {
    Optional<Payments> findByOrderId(String orderId);
}
