package com.loopers.infrastructure.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loopers.domain.payment.Payments;

public interface PaymentJpaRepository extends JpaRepository<Payments, Long> {
}
