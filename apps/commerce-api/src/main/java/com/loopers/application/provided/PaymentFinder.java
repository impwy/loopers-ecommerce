package com.loopers.application.provided;

import java.util.List;

import com.loopers.domain.payment.Payments;

public interface PaymentFinder {
    List<Payments> getPayments(String orderId);
}
