package com.loopers.application.payment;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.MemberFinder;
import com.loopers.application.provided.OrderFinder;
import com.loopers.application.provided.OrderRegister;
import com.loopers.application.provided.PaymentRegister;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;
import com.loopers.domain.order.Order;
import com.loopers.domain.payment.PaymentStatus;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Request.PaymentRequest;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Response.TransactionDetailResponse;
import com.loopers.interfaces.api.payment.dto.PaymentV1Dto.Response.TransactionResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentFacade {
    private final PaymentRegister paymentRegister;
    private final MemberFinder memberFinder;
    private final OrderFinder orderFinder;
    private final OrderRegister orderRegister;

    // 결제 요청
    @Transactional
    public void requestPayment(MemberId memberId, PaymentRequest paymentRequest) {
        Member member = memberFinder.findByMemberId(memberId);
        Order order = orderFinder.find(paymentRequest.orderId());
        paymentRegister.createPayment(member.getId(), paymentRequest);
        paymentRegister.requestPayment(order.getOrderNo().value(), memberId, paymentRequest);
    }

    // 결제 콜백
    @Transactional
    public void callback(MemberId memberId, TransactionResponse transactionResponse) {
        // 결제 상태 조회
        TransactionDetailResponse paymentDetailResponse =
                paymentRegister.getPaymentDetailResponse(memberId, transactionResponse);
        PaymentStatus paymentStatus = paymentDetailResponse.status();

        String orderId = paymentDetailResponse.orderId();

        switch (paymentStatus) {
            case SUCCESS -> {
                paymentRegister.successPayment(orderId);
                orderRegister.successOrder(orderId);
            }
            case FAILED -> {
                paymentRegister.failPayment(orderId);
                orderRegister.failOrder(orderId);
            }
        }
    }
}
