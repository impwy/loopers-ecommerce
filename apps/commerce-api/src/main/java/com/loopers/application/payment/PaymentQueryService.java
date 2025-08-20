package com.loopers.application.payment;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.PaymentFinder;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentQueryService implements PaymentFinder {
}
