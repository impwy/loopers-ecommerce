package com.loopers.application.payment;

import org.springframework.stereotype.Component;

import com.loopers.application.provided.MemberFinder;
import com.loopers.application.provided.OrderFinder;
import com.loopers.application.provided.PaymentRegister;
import com.loopers.domain.member.Member;
import com.loopers.domain.member.MemberId;
import com.loopers.domain.order.Order;
import com.loopers.domain.payment.PaymentGateway;
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
    private final PaymentGateway paymentGateway;

    // 결제 요청
    @Transactional
    public void requestPayment(MemberId memberId, PaymentRequest paymentRequest) {
        Member member = memberFinder.findByMemberId(memberId);
        Order order = orderFinder.find(paymentRequest.orderId());
        paymentRegister.createPayment(member.getId(), paymentRequest);
        paymentRegister.requestPayment(order.getOrderNo().value(), memberId, paymentRequest);
    }

    // 결제 콜백
    public void callback(MemberId memberId, TransactionResponse transactionResponse) {
        // 결제 상태를 조회한다.
        TransactionDetailResponse transactionDetailResponse
                = paymentGateway.getPaymentDetailResponse(memberId, transactionResponse.transactionKey());

        PaymentStatus paymentStatus = transactionDetailResponse.status();

        if (PaymentStatus.SUCCESS == paymentStatus) {

        } else if (PaymentStatus.FAILED == paymentStatus) {

        } else if (PaymentStatus.PENDING == paymentStatus) {

        }
    }
}
