package com.loopers.domain.payment.paymentrule;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.MemberRegister;
import com.loopers.application.provided.OrderRegister;
import com.loopers.application.provided.PaymentRegister;
import com.loopers.domain.member.MemberId;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component("POINT")
@RequiredArgsConstructor
public class PointPaymentService implements PaymentService {
    private final OrderRegister orderRegister;
    private final PaymentRegister paymentRegister;
    private final MemberRegister memberRegister;

    @Transactional
    @Override
    public void requestPayment(String orderId, MemberId memberId, PaymentRequest payment) {
        // 포인트를 차감한다.
        memberRegister.usePoint(memberId, payment.totalAmount());
        orderRegister.successOrder(orderId);
        paymentRegister.successPayment(orderId);
    }
}
