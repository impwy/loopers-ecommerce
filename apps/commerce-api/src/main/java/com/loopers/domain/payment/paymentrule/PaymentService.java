package com.loopers.domain.payment.paymentrule;

import com.loopers.domain.member.MemberId;

public interface PaymentService {
    <T> void requestPayment(String orderId, MemberId memberId, T payment);
}
