package com.loopers.application.payment;

import java.util.List;

import org.springframework.stereotype.Component;

import com.loopers.application.payment.provided.PaymentFinder;
import com.loopers.application.payment.required.PaymentRepository;
import com.loopers.domain.payment.Payments;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentQueryService implements PaymentFinder {
    private final PaymentRepository paymentRepository;

    @Override
    public List<Payments> getPayments(String orderId) {
        return paymentRepository.findALlByOrderId(orderId);
    }
}
