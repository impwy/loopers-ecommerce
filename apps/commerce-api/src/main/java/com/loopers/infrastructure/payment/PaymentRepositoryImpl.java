package com.loopers.infrastructure.payment;

import org.springframework.stereotype.Repository;

import com.loopers.application.required.PaymentRepository;
import com.loopers.domain.payment.Payments;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payments save(Payments payments) {
        return paymentJpaRepository.save(payments);
    }
}
