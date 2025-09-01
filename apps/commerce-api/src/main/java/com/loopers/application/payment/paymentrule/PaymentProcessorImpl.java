package com.loopers.application.payment.paymentrule;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.loopers.domain.payment.PaymentType;

@Component
public class PaymentProcessorImpl implements PaymentProcessor {
    Map<PaymentType, PaymentService> paymentServiceMap;

    public PaymentProcessorImpl(Map<String, PaymentService> paymentServiceMap) {
        this.paymentServiceMap = paymentServiceMap.entrySet().stream()
                                                  .collect(Collectors.toMap(entry -> PaymentType.of(entry.getKey()),
                                                                            Entry::getValue));
    }

    @Override
    public PaymentService getProcessor(PaymentType paymentType) {
        PaymentService paymentService = paymentServiceMap.get(paymentType);
        if (paymentService == null) {
            throw new IllegalArgumentException("지원하지 않는 결제 방법입니다.");
        }
        return paymentService;
    }
}
