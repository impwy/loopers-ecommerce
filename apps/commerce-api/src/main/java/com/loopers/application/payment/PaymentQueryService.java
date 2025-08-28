package com.loopers.application.payment;

import java.util.List;

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
    public List<Payments> getPayments(String orderId) {
        return paymentRepository.findALlByOrderId(orderId);
    }
}
