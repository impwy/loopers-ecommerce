package com.loopers.domain.payment.paymentrule;

import com.loopers.domain.member.MemberId;

public interface PaymentService {
    <T> void pay(String orderId, MemberId memberId, T payment);
}
