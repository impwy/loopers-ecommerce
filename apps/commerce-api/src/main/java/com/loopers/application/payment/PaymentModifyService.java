package com.loopers.application.payment;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.OrderFinder;
import com.loopers.application.provided.PaymentRegister;
import com.loopers.application.required.PaymentRepository;
import com.loopers.domain.member.MemberId;
import com.loopers.domain.order.Order;
import com.loopers.domain.payment.CreatePaymentSpec;
import com.loopers.domain.payment.PaymentGateway;
import com.loopers.domain.payment.Payments;
import com.loopers.domain.payment.paymentrule.PaymentProcessor;
import com.loopers.domain.payment.paymentrule.PaymentService;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Response.TransactionDetailResponse;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Response.TransactionResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentModifyService implements PaymentRegister {
    private final PaymentRepository paymentRepository;
    private final PaymentProcessor paymentProcessor;
    private final OrderFinder orderFinder;
    private final PaymentGateway paymentGateway;

    @Override
    public Payments createPayment(Long memberId, PaymentRequest paymentRequest) {
        Order order = orderFinder.find(paymentRequest.orderId());
        CreatePaymentSpec createPaymentSpec = CreatePaymentSpec.of(order.getOrderNo().value(), memberId, paymentRequest);
        Payments payments = Payments.create(createPaymentSpec);
        return paymentRepository.save(payments);
    }

    @Override
    public void requestPayment(String orderId, MemberId memberId, PaymentRequest paymentRequest) {
        PaymentService paymentService = paymentProcessor.getProcessor(paymentRequest.paymentType());
        paymentService.requestPayment(orderId, memberId, paymentRequest);
    }

    @Override
    public TransactionDetailResponse getPaymentDetailResponse(MemberId memberId, TransactionResponse transactionResponse) {
        return paymentGateway.getPaymentDetailResponse(memberId, transactionResponse.transactionKey());
    }
}
