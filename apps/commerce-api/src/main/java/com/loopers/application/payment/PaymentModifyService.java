package com.loopers.application.payment;

import java.util.List;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.OrderFinder;
import com.loopers.application.provided.PaymentFinder;
import com.loopers.application.provided.PaymentRegister;
import com.loopers.application.required.PaymentRepository;
import com.loopers.domain.member.MemberId;
import com.loopers.domain.order.Order;
import com.loopers.domain.payment.CreatePaymentSpec;
import com.loopers.domain.payment.PaymentGateway;
import com.loopers.domain.payment.Payments;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Response.TransactionDetailResponse;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Response.TransactionResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentModifyService implements PaymentRegister {
    private final PaymentRepository paymentRepository;
    private final OrderFinder orderFinder;
    private final PaymentGateway paymentGateway;
    private final PaymentFinder paymentFinder;

    @Override
    public Payments createPayment(Long memberId, PaymentRequest paymentRequest) {
        Order order = orderFinder.find(paymentRequest.orderId());
        CreatePaymentSpec createPaymentSpec = CreatePaymentSpec.of(order.getOrderNo().value(), memberId, paymentRequest);
        Payments payments = Payments.create(createPaymentSpec);
        return paymentRepository.save(payments);
    }

    @Override
    public TransactionDetailResponse getPaymentDetailResponse(MemberId memberId, TransactionResponse transactionResponse) {
        return paymentGateway.getPaymentDetailResponse(memberId, transactionResponse.transactionKey());
    }

    @Override
    public void successPayment(String orderId) {
        List<Payments> payments = paymentFinder.getPayments(orderId);
        payments.forEach(Payments::successPayments);
        paymentRepository.saveAll(payments);
    }

    @Override
    public void failPayment(String orderId) {
        List<Payments> payments = paymentFinder.getPayments(orderId);
        payments.forEach(Payments::failPayments);
        paymentRepository.saveAll(payments);
    }
}
