package com.loopers.application.payment;

import com.loopers.adapter.webapi.payment.dto.PaymentV1Dto.Request.PaymentRequest;
import com.loopers.adapter.webapi.payment.dto.PaymentV1Dto.Response.TransactionDetailResponse;
import com.loopers.adapter.webapi.payment.dto.PaymentV1Dto.Response.TransactionResponse;
import com.loopers.application.member.provided.MemberFinder;
import com.loopers.application.order.provided.OrderFinder;
import com.loopers.application.payment.paymentrule.PaymentProcessor;
import com.loopers.application.payment.paymentrule.PaymentService;
import com.loopers.application.payment.provided.PaymentRegister;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.UserId;
import com.loopers.domain.order.Order;
import com.loopers.domain.payment.PaymentStatus;
import com.loopers.shared.stereotype.ApplicationValidService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationValidService
@RequiredArgsConstructor
public class PaymentFacade {
    private final PaymentRegister paymentRegister;
    private final MemberFinder memberFinder;
    private final OrderFinder orderFinder;
    private final PaymentProcessor paymentProcessor;
    private final PaymentSuccessHandler paymentSuccessHandler;
    private final PaymentFailureHandler paymentFailureHandler;

    // 결제 요청
    @Transactional
    public void requestPayment(UserId userId, PaymentRequest paymentRequest) {
        Member member = memberFinder.findWithPoint(userId);
        Order order = orderFinder.find(paymentRequest.orderId());
        paymentRegister.createPayment(member.getId(), paymentRequest);

        PaymentService paymentService = paymentProcessor.getProcessor(paymentRequest.paymentType());
        paymentService.requestPayment(order, member, paymentRequest);
    }

    // 결제 콜백
    @Transactional
    public void callback(UserId userId, TransactionResponse transactionResponse) {
        // 결제 상태 조회
        TransactionDetailResponse paymentDetailResponse =
                paymentRegister.getPaymentDetailResponse(userId, transactionResponse);
        PaymentStatus paymentStatus = paymentDetailResponse.status();

        String orderId = paymentDetailResponse.orderId();

        switch (paymentStatus) {
            case SUCCESS -> {
                paymentSuccessHandler.handle(orderId);
            }
            case FAILED -> {
                paymentFailureHandler.handle(userId, orderId);
            }
        }
    }
}
