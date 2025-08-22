package com.loopers.application.payment;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.PaymentFinder;
import com.loopers.application.required.PaymentRepository;
import com.loopers.domain.payment.Payments;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentQueryService implements PaymentFinder {
    private final PaymentRepository paymentRepository;

    @Override
    public Payments getPayments(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다."));
    }
}
