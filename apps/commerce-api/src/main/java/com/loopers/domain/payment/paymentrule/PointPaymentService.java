package com.loopers.domain.payment.paymentrule;

import java.util.List;

import org.springframework.stereotype.Component;

import com.loopers.application.required.MemberRepository;
import com.loopers.application.required.OrderRepository;
import com.loopers.application.required.PaymentRepository;
import com.loopers.domain.member.Member;
import com.loopers.domain.order.Order;
import com.loopers.domain.payment.Payments;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component("POINT")
@RequiredArgsConstructor
public class PointPaymentService implements PaymentService {
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    @Override
    public void requestPayment(Order order, Member member, PaymentRequest payment) {
        // 포인트를 차감한다.
        member.usePoint(payment.totalAmount());
        order.successOrder();

        List<Payments> payments = paymentRepository.findALlByOrderId(order.getOrderNo().value());
        payments.forEach(Payments::successPayments);

        paymentRepository.saveAll(payments);
        memberRepository.save(member);
        orderRepository.save(order);
    }
}
