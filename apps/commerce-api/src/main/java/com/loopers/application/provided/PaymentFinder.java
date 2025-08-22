package com.loopers.application.provided;

import com.loopers.domain.payment.Payments;

public interface PaymentFinder {
    Payments getPayments(String orderId);
}
