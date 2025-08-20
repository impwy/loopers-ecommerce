package com.loopers.application.payment;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.PaymentRegister;
import com.loopers.application.required.PaymentRepository;
import com.loopers.domain.member.MemberId;
import com.loopers.domain.payment.CreatePaymentSpec;
import com.loopers.domain.payment.Payments;
import com.loopers.domain.payment.paymentrule.PaymentProcessor;
import com.loopers.domain.payment.paymentrule.PaymentService;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentModifyService implements PaymentRegister {
    private final PaymentRepository paymentRepository;
    private final PaymentProcessor paymentProcessor;

    @Override
    public Payments createPayment(Long memberId, PaymentRequest paymentRequest) {
        CreatePaymentSpec createPaymentSpec = CreatePaymentSpec.of(memberId, paymentRequest);
        Payments payments = Payments.create(createPaymentSpec);
        return paymentRepository.save(payments);
    }

    @Override
    public void requestPayment(MemberId memberId, PaymentRequest paymentRequest) {
        PaymentService paymentService = paymentProcessor.getProcessor(paymentRequest.paymentType());
        // TODO: paymentService.pay(); 결제 정보 만들어 넘겨주기.
    }
}
