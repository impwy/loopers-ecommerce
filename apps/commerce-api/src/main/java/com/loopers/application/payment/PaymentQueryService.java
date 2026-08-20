package com.loopers.application.payment;

import java.util.List;

import com.loopers.application.payment.provided.PaymentFinder;
import com.loopers.application.payment.required.PaymentRepository;
import com.loopers.domain.payment.Payments;
import com.loopers.shared.stereotype.ApplicationService;

import lombok.RequiredArgsConstructor;

@ApplicationService
@RequiredArgsConstructor
public class PaymentQueryService implements PaymentFinder {
    private final PaymentRepository paymentRepository;

    @Override
    public List<Payments> getPayments(String orderId) {
        return paymentRepository.findAllByOrderId(orderId);
    }
}
