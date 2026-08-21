package com.loopers.application.payment;

import java.util.List;

import com.loopers.application.order.provided.OrderFinder;
import com.loopers.application.payment.provided.PaymentFinder;
import com.loopers.application.payment.provided.PaymentRegister;
import com.loopers.application.payment.required.PaymentGateway;
import com.loopers.application.payment.required.PaymentRepository;
import com.loopers.domain.member.UserId;
import com.loopers.domain.order.Order;
import com.loopers.domain.payment.CreatePaymentSpec;
import com.loopers.domain.payment.Payments;
import com.loopers.shared.stereotype.ApplicationValidService;

import lombok.RequiredArgsConstructor;

@ApplicationValidService
@RequiredArgsConstructor
public class PaymentModifyService implements PaymentRegister {
    private final PaymentRepository paymentRepository;
    private final OrderFinder orderFinder;
    private final PaymentGateway paymentGateway;
    private final PaymentFinder paymentFinder;

    @Override
    public Payments createPayment(Long memberId, PaymentRequest paymentRequest) {
        Order order = orderFinder.find(paymentRequest.orderId());
        CreatePaymentSpec createPaymentSpec = CreatePaymentSpec.of(order.getOrderNo().value(), memberId,
                                                                  paymentRequest.cardType(), paymentRequest.cardNo(),
                                                                  paymentRequest.totalAmount(), paymentRequest.paymentType());
        Payments payments = Payments.create(createPaymentSpec);
        return paymentRepository.save(payments);
    }

    @Override
    public PaymentDetailResult getPaymentDetailResponse(UserId userId, PaymentCallbackRequest callbackRequest) {
        return paymentGateway.getPaymentDetailResponse(userId, callbackRequest.transactionKey());
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
