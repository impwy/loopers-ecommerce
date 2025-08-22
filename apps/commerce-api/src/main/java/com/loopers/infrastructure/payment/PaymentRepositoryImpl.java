package com.loopers.infrastructure.payment;

import java.util.List;
import java.util.Optional;

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

    @Override
    public List<Payments> saveAll(List<Payments> payments) {
        return paymentJpaRepository.saveAll(payments);
    }

    @Override
    public Optional<Payments> findByOrderId(String orderId) {
        return paymentJpaRepository.findByOrderId(orderId);
    }

    @Override
    public List<Payments> findALlByOrderId(String orderId) {
        return paymentJpaRepository.findAllByOrderId(orderId);
    }
}
